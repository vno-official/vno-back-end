package com.vno.auth.service;

import com.vno.core.entity.Role;
import com.vno.core.entity.User;
import com.vno.core.entity.UserOrganization;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
     * Claims: user_id, email, org_id, role, orgs[]
     */
    public Uni<String> generateToken(User user, UUID currentOrgId) {
        return UserOrganization.findRoleByUserAndOrg(user.id, currentOrgId)
            .onItem().ifNull().failWith(() -> 
                new IllegalArgumentException("User does not belong to organization"))
            .flatMap(role -> 
                // Fetch user organizations with organization eagerly loaded (FIX LAZY LOADING)
                UserOrganization.<UserOrganization>find(
                    "SELECT uo FROM UserOrganization uo " +
                    "JOIN FETCH uo.organization " +
                    "WHERE uo.user.id = ?1", user.id
                ).list()
                .map(userOrgs -> buildJwtToken(user, currentOrgId, role, userOrgs))
            );
    }

    private String buildJwtToken(User user, UUID currentOrgId, Role role, List<UserOrganization> userOrgs) {
        // Build orgs array for org switcher
        JsonArray orgsArray = new JsonArray();
        for (UserOrganization uo : userOrgs) {
            JsonObject orgObj = new JsonObject()
                .put("id", uo.organization.id.toString())
                .put("name", uo.organization.name)
                .put("subdomain", uo.organization.slug)
                .put("role", uo.role.name());
            orgsArray.add(orgObj);
        }

        // Add role to groups for @RolesAllowed
        Set<String> groups = new HashSet<>();
        groups.add(role.name());

        return Jwt.issuer("vno-backend")
                .subject(user.id.toString())
                .claim("user_id", user.id.toString())
                .claim("email", user.email)
                .claim("name", user.name)
                .claim("org_id", currentOrgId.toString())
                .claim("role", role.name())
                .claim("orgs", orgsArray)
                .groups(groups)
                .expiresIn(Duration.ofMinutes(accessTokenExpiryMinutes))
                .sign();  // Sign with configured private key
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
