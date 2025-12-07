package com.vno.auth.service;

import com.vno.core.entity.Organization;
import com.vno.core.entity.Role;
import com.vno.core.entity.User;
import com.vno.core.entity.UserOrganization;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "app.jwt.access-token-expiry-minutes", defaultValue = "15")
    int accessTokenExpiryMinutes;

    /**
     * Generate JWT with full multi-org claims (Reactive)
     * Claims: user (full object), currentOrganization (full object), organizations array
     */
    public Uni<String> generateToken(User user, UUID currentOrgId) {
        return UserOrganization.findRoleByUserAndOrg(user.id, currentOrgId)
            .onItem().ifNull().failWith(() -> 
                new IllegalArgumentException("User does not belong to organization"))
            .flatMap(role -> 
                // Fetch user organizations with organization eagerly loaded
                UserOrganization.<UserOrganization>find(
                    "SELECT uo FROM UserOrganization uo " +
                    "JOIN FETCH uo.organization " +
                    "WHERE uo.user.id = ?1", user.id
                ).list()
                .flatMap(userOrgs -> 
                    // Find current organization
                    Organization.<Organization>findById(currentOrgId)
                        .map(currentOrg -> buildJwtToken(user, currentOrg, role, userOrgs))
                )
            );
    }

    private String buildJwtToken(User user, Organization currentOrg, Role role, List<UserOrganization> userOrgs) {
        // Build user object as Map for proper JSON serialization
        java.util.Map<String, Object> userObj = new java.util.HashMap<>();
        userObj.put("id", user.id.toString());
        userObj.put("email", user.email);
        userObj.put("name", user.name);
        userObj.put("avatarUrl", user.avatarUrl);
        userObj.put("createdAt", user.createdAt != null ? user.createdAt.toString() : null);

        // Build current organization object as Map
        java.util.Map<String, Object> currentOrgObj = new java.util.HashMap<>();
        currentOrgObj.put("id", currentOrg.id.toString());
        currentOrgObj.put("slug", currentOrg.slug);
        currentOrgObj.put("name", currentOrg.name);
        currentOrgObj.put("plan", currentOrg.plan);
        currentOrgObj.put("createdAt", currentOrg.createdAt != null ? currentOrg.createdAt.toString() : null);

        // Build organizations array as List of Maps
        java.util.List<java.util.Map<String, Object>> orgsArray = new java.util.ArrayList<>();
        for (UserOrganization uo : userOrgs) {
            java.util.Map<String, Object> orgObj = new java.util.HashMap<>();
            orgObj.put("id", uo.organization.id.toString());
            orgObj.put("slug", uo.organization.slug);
            orgObj.put("name", uo.organization.name);
            orgObj.put("plan", uo.organization.plan);
            orgObj.put("createdAt", uo.organization.createdAt != null ? uo.organization.createdAt.toString() : null);
            orgObj.put("role", uo.role.name());
            orgsArray.add(orgObj);
        }

        // Add role to groups for @RolesAllowed
        Set<String> groups = new HashSet<>();
        groups.add(role.name());

        // JWT with full user and organization data as proper JSON objects
        return Jwt.issuer("vno-backend")
                .subject(user.id.toString())
                .claim("user", userObj)
                .claim("currentOrganization", currentOrgObj)
                .claim("organizations", orgsArray)
                .claim("role", role.name())
                .groups(groups)
                .expiresIn(Duration.ofMinutes(accessTokenExpiryMinutes))
                .sign();
    }

    /**
     * Generate JWT for specific organization (used for org switching)
     */
    public Uni<String> generateTokenForOrg(User user, UUID orgId) {
        return UserOrganization.userBelongsToOrg(user.id, orgId)
            .flatMap(belongs -> {
                if (!belongs) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("User does not belong to organization"));
                }
                return generateToken(user, orgId);
            });
    }
}
