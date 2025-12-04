package com.vno.auth.service;

import com.vno.core.entity.Role;
import com.vno.core.entity.User;
import com.vno.core.entity.UserOrganization;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "app.jwt.expiry-hours", defaultValue = "168")
    int expiryHours;

    /**
     * Generate JWT with full multi-org claims (Reactive)
     * Claims: user_id, email, org_id, role, orgs[]
     */
    public Uni<String> generateToken(User user, Long currentOrgId) {
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

    private String buildJwtToken(User user, Long currentOrgId, Role role, List<UserOrganization> userOrgs) {
        // Build orgs array for org switcher
        JsonArrayBuilder orgsArray = Json.createArrayBuilder();
        for (UserOrganization uo : userOrgs) {
            JsonObjectBuilder orgObj = Json.createObjectBuilder()
                .add("id", uo.organization.id)
                .add("name", uo.organization.name)
                .add("subdomain", uo.organization.slug)
                .add("role", uo.role.name());
            orgsArray.add(orgObj);
        }

        // Add role to groups for @RolesAllowed
        Set<String> groups = new HashSet<>();
        groups.add(role.name());

        return Jwt.issuer("vno-backend")
                .subject(user.id.toString())
                .claim("user_id", user.id)
                .claim("email", user.email)
                .claim("name", user.name)
                .claim("org_id", currentOrgId)
                .claim("role", role.name())
                .claim("orgs", orgsArray.build())
                .groups(groups)
                .expiresIn(Duration.ofHours(expiryHours))
                .sign();  // Sign with configured private key
    }

    /**
     * Generate JWT for specific organization (used for org switching)
     */
    public Uni<String> generateTokenForOrg(User user, Long orgId) {
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
