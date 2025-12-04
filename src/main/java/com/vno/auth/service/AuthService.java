package com.vno.auth.service;

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
    public Uni<String> switchOrganization(Long userId, Long targetOrgId) {
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
}
