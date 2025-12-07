package com.vno.auth.service;

import java.time.Instant;
import java.util.UUID;

import com.vno.auth.email.PasswordResetEmailTemplate;
import com.vno.core.entity.PasswordResetToken;
import com.vno.core.entity.User;
import com.vno.core.entity.UserOrganization;
import com.vno.org.service.OrgBootstrapService;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

// import com.vno.core.entity.Organization;
// import com.vno.core.entity.User;
// import com.vno.core.entity.UserOrganization;
// import com.vno.org.service.OrgBootstrapService;
// import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
// import io.smallrye.mutiny.Uni;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;
// import jakarta.ws.rs.BadRequestException;

/**
 * Reactive authentication service for username/password auth
 */
@ApplicationScoped
public class AuthService {

    @Inject
    OrgBootstrapService orgBootstrapService;

    @Inject
    JwtService jwtService;

    @Inject
    EmailService emailService;

    /**
     * Register new user with username/password (Reactive)
     */
    @WithTransaction
    public Uni<String> registerUser(String email, String password, String name) {
        return User.findByEmail(email)
            .flatMap(existingUser -> {
                if (existingUser != null) {
                    return Uni.createFrom().failure(
                        new BadRequestException("User with this email already exists"));
                }
                
                // Create new user
                User user = new User();
                user.email = email;
                user.name = name;
                user.setPassword(password);
                
                return user.persistAndFlush()
                    .flatMap(u -> orgBootstrapService.bootstrapOrganization(user))
                    .flatMap(org -> jwtService.generateToken(user, org.id));
            });
    }

    /**
     * Login with username/password (Reactive)
     */
    @WithTransaction
    public Uni<String> loginUser(String email, String password) {
        return User.findByEmail(email)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Invalid email or password"))
            .onItem().ifNotNull().transformToUni(user -> {
                if (!user.verifyPassword(password)) {
                    return Uni.createFrom().failure(
                        new BadRequestException("Invalid email or password"));
                }
                
                // Get user's first organization
                return user.hasAnyOrganization()
                    .flatMap(hasOrg -> {
                        if (!hasOrg) {
                            return orgBootstrapService.bootstrapOrganization(user);
                        }
                        return user.getOrganizations()
                            .map(orgs -> orgs.get(0));
                    })
                    .flatMap(org -> jwtService.generateToken(user, org.id));
            });
    }

    /**
     * Switch organization - return new JWT (Reactive)
     */
    public Uni<String> switchOrganization(UUID userId, UUID targetOrgId) {
        return User.<User>findById(userId)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("User not found"))
            .flatMap(user -> 
                UserOrganization.userBelongsToOrg(userId, targetOrgId)
                    .flatMap(belongs -> {
                        if (!belongs) {
                            return Uni.createFrom().failure(
                                new BadRequestException("User does not belong to organization"));
                        }
                        return jwtService.generateToken(user, targetOrgId);
                    })
            );
    }

    /**
     * Change user password (requires current password verification)
     */
    @WithTransaction
    public Uni<Void> changePassword(UUID userId, String currentPassword, String newPassword) {
        return User.<User>findById(userId)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("User not found"))
            .flatMap(user -> {
                // Verify current password
                if (!user.verifyPassword(currentPassword)) {
                    return Uni.createFrom().failure(
                        new BadRequestException("Current password is incorrect"));
                }
                
                // Update to new password
                user.setPassword(newPassword);
                return user.persistAndFlush()
                    .replaceWith(Uni.createFrom().voidItem());
            });
    }

    /**
     * Request password reset - generates token and sends email
     */
    @WithTransaction
    public Uni<Void> requestPasswordReset(String email) {
        return User.findByEmail(email)
            .flatMap(user -> {
                // Silently succeed if user not found (security: prevent email enumeration)
                if (user == null) {
                    return Uni.createFrom().voidItem();
                }
                
                // Generate reset token
                String token = UUID.randomUUID().toString();
                Instant expiresAt = Instant.now().plusSeconds(3600); // 1 hour
                
                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.user = user;
                resetToken.token = token;
                resetToken.expiresAt = expiresAt;
                
                // Build reset link
                String resetLink = String.format("https://app.vno.com/reset-password?token=%s", token);
                PasswordResetEmailTemplate emailTemplate = new PasswordResetEmailTemplate(resetLink);
                
                // Save token and send email
                return resetToken.persistAndFlush()
                    .flatMap(savedToken -> 
                        emailService.sendEmailReactive(email, emailTemplate, "VNO")
                            .replaceWith(Uni.createFrom().voidItem())
                    )
                    .onFailure().recoverWithItem(() -> {
                        // Log but don't fail the entire operation if email fails
                        return null;
                    });
            });
    }

    /**
     * Reset password using valid token
     */
    @WithTransaction
    public Uni<Void> resetPassword(String token, String newPassword) {
        return PasswordResetToken.findValidToken(token)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Invalid or expired reset token"))
            .flatMap(resetToken -> {
                return User.<User>findById(resetToken.user.id)
                    .onItem().ifNull().failWith(() -> 
                        new BadRequestException("User not found"))
                    .flatMap(user -> {
                        user.setPassword(newPassword);
                        resetToken.markAsUsed();
                        
                        // Save both entities
                        return user.persistAndFlush()
                            .flatMap(u -> resetToken.persistAndFlush())
                            .replaceWith(Uni.createFrom().voidItem());
                    });
                // Get user and update password
            });
    }
}
