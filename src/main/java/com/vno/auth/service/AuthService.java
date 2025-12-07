package com.vno.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.vno.auth.dto.AuthTokens;
import com.vno.auth.email.PasswordResetEmailTemplate;
import com.vno.core.entity.Organization;
import com.vno.core.entity.PasswordResetToken;
import com.vno.core.entity.RefreshToken;
import com.vno.core.entity.User;
import com.vno.core.entity.UserOrganization;
import com.vno.org.service.OrgBootstrapService;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

/**
 * Authentication service handling user registration, login, and token management.
 * Follows best practices for security and reactive programming.
 */
@ApplicationScoped
public class AuthService {

    @Inject
    OrgBootstrapService orgBootstrapService;

    @Inject
    JwtService jwtService;

    @Inject
    EmailService emailService;

    @ConfigProperty(name = "app.jwt.refresh-token-expiry-days", defaultValue = "7")
    int refreshTokenExpiryDays;

    // ==================== User Registration & Login ====================

    /**
     * Register a new user with email/password and create their first organization.
     * Returns both access token and refresh token.
     */
    @WithTransaction
    public Uni<AuthTokens> registerUser(String email, String password, String name) {
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
                    .flatMap(org -> generateAuthTokens(user, org));
            });
    }

    /**
     * Authenticate user with email/password.
     * Returns both access token and refresh token.
     */
    @WithTransaction
    public Uni<AuthTokens> loginUser(String email, String password) {
        return User.findByEmail(email)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Invalid email or password"))
            .onItem().ifNotNull().transformToUni(user -> {
                // Verify password
                if (!user.verifyPassword(password)) {
                    return Uni.createFrom().failure(
                        new BadRequestException("Invalid email or password"));
                }
                
                // Get or create organization
                return user.hasAnyOrganization()
                    .flatMap(hasOrg -> {
                        if (!hasOrg) {
                            return orgBootstrapService.bootstrapOrganization(user);
                        }
                        return user.getOrganizations()
                            .map(orgs -> orgs.get(0));
                    })
                    .flatMap(org -> generateAuthTokens(user, org));
            });
    }

    // ==================== Token Management ====================

    /**
     * Generate both access and refresh tokens for a user in a specific organization.
     * Also fetches and returns complete user and organization data.
     */
    private Uni<AuthTokens> generateAuthTokens(User user, Organization currentOrg) {
        return createRefreshToken(user)
            .flatMap(refreshToken -> 
                jwtService.generateToken(user, currentOrg.id)
                    .map(accessToken -> 
                        new AuthTokens(accessToken, refreshToken, user, currentOrg)
                    )
            );
    }

    /**
     * Create and persist a new refresh token.
     */
    private Uni<String> createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenExpiryDays * 24 * 60 * 60);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.user = user;
        refreshToken.token = token;
        refreshToken.expiresAt = expiresAt;
        
        return refreshToken.persistAndFlush()
            .map(rt -> token);
    }

    /**
     * Refresh access token using a valid refresh token.
     * Returns a new access token while keeping the same refresh token.
     */
    @WithTransaction
    public Uni<String> refreshAccessToken(String refreshTokenString) {
        return RefreshToken.findValidToken(refreshTokenString)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Invalid or expired refresh token"))
            .flatMap(refreshToken -> {
                // Update last used timestamp
                refreshToken.updateLastUsed();
                
                UUID userId = refreshToken.user.id;
                
                return refreshToken.persistAndFlush()
                    .flatMap(rt -> User.<User>findById(userId))
                    .onItem().ifNull().failWith(() -> 
                        new BadRequestException("User not found"))
                    .flatMap(user -> 
                        user.getOrganizations()
                            .map(orgs -> orgs.isEmpty() ? null : orgs.get(0))
                            .flatMap(org -> {
                                if (org == null) {
                                    return Uni.createFrom().failure(
                                        new BadRequestException("User has no organizations"));
                                }
                                return jwtService.generateToken(user, org.id);
                            })
                    );
            });
    }

    /**
     * Revoke a refresh token (used for logout).
     */
    @WithTransaction
    public Uni<Void> revokeRefreshToken(String refreshTokenString) {
        return RefreshToken.findByToken(refreshTokenString)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Refresh token not found"))
            .flatMap(refreshToken -> {
                refreshToken.revoke();
                return refreshToken.persistAndFlush()
                    .replaceWith(Uni.createFrom().voidItem());
            });
    }

    // ==================== Organization Switching ====================

    /**
     * Switch to a different organization.
     * Returns a new access token for the target organization.
     */
    @WithTransaction
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

    // ==================== Password Management ====================

    /**
     * Change user password (requires current password verification).
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
     * Request password reset - generates token and sends email.
     * Silently succeeds even if email doesn't exist (prevents email enumeration).
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
     * Reset password using a valid reset token.
     */
    @WithTransaction
    public Uni<Void> resetPassword(String token, String newPassword) {
        return PasswordResetToken.findValidToken(token)
            .onItem().ifNull().failWith(() -> 
                new BadRequestException("Invalid or expired reset token"))
            .flatMap(resetToken -> 
                User.<User>findById(resetToken.user.id)
                    .onItem().ifNull().failWith(() -> 
                        new BadRequestException("User not found"))
                    .flatMap(user -> {
                        user.setPassword(newPassword);
                        resetToken.markAsUsed();
                        
                        // Save both entities
                        return user.persistAndFlush()
                            .flatMap(u -> resetToken.persistAndFlush())
                            .replaceWith(Uni.createFrom().voidItem());
                    })
            );
    }
}
