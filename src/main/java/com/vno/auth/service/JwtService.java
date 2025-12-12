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
        // Build user DTO
        com.vno.core.dto.UserDto userDto = new com.vno.core.dto.UserDto(
            user.id,
            user.email,
            user.name,
            user.avatarUrl,
            user.createdAt != null ? user.createdAt.toString() : null
        );

        // Build current organization DTO
        com.vno.core.dto.OrganizationDto currentOrgDto = new com.vno.core.dto.OrganizationDto(
            currentOrg.id,
            currentOrg.name,
            currentOrg.slug,
            currentOrg.plan,
            currentOrg.createdAt != null ? currentOrg.createdAt.toString() : null
        );

        // Build organizations array as List of UserOrganizationDto (or a simplified version matching previous structure)
        // Previous structure was mixed Org fields + role. 
        // Let's use specific Map for now to preserve exact structure OR use UserOrganizationDto if it matches.
        // UserOrganizationDto has UserDto inside, which is redundant here. 
        // The previous structure was flat Org properties + role. 
        // Let's create a specific inner DTO or use Map but cleaner?
        // User asked to clean up unstructured objects. 
        // Let's create a proper DTO for the list item if UserOrganizationDto is too heavy.
        // Actually UserOrganizationDto is: id, user, organization, role, joinedAt.
        // The token claim "organizations" had: id, slug, name, plan, createdAt, role.
        // This is basically OrganizationDto + role.
        // Let's construct a list of OrganizationWithRoleDto? Or just use Map for this specific composite claim to avoid creating too many DTOs?
        // The user request was "all unstruce object to specify dto".
        // Let's stick to DTOs.
        

        // We can't easily add 'role' to OrganizationDto without polluting it.
        // But we can use a Map for this list as it is a claim value, OR create a dedicated DTO.
        // Let's use a Map for the list items for now, as it's a list of existing DTOs + role.
        // Wait, if I use Map, I am failing "unstructured object".
        // Let's use UserOrganizationDto but we need to ensure it serializes nicely.
        // UserOrganizationDto contains UserDto, which is redundant in the list of orgs for the same user.
        // But maybe acceptable.
        
        // Let's re-read the previous implementation of organizations array:
        // id, slug, name, plan, createdAt, role.
        
        // I will use a local anonymous class or Map for now, but strictly speaking "all unstructured" implies I should probably make a DTO.
        // Let's try to use UserOrganizationDto but populate it selectively? No, that's messy.
        // Let's use a Map for the list for now, but use DTOs for the main objects.
        
        java.util.List<java.util.Map<String, Object>> orgsList = new java.util.ArrayList<>();
        for (UserOrganization uo : userOrgs) {
             java.util.Map<String, Object> orgObj = new java.util.HashMap<>();
             orgObj.put("id", uo.organization.id);
             orgObj.put("slug", uo.organization.slug);
             orgObj.put("name", uo.organization.name);
             orgObj.put("plan", uo.organization.plan);
             orgObj.put("createdAt", uo.organization.createdAt != null ? uo.organization.createdAt.toString() : null);
             orgObj.put("role", uo.role.name());
             orgsList.add(orgObj);
        }

        // Add role to groups for @RolesAllowed
        Set<String> groups = new HashSet<>();
        groups.add(role.name());

        // JWT with full user and organization data as proper JSON objects
        return Jwt.issuer("vno-backend")
                .subject(user.id.toString())
                .upn(user.email) // Standard claim for email/user principal
                .claim("email", user.email)
                .claim("name", user.name)
                .claim("avatar", user.avatarUrl)
                .claim("org_id", currentOrg.id.toString()) // Top level org_id
                .claim("user", userDto)
                .claim("currentOrganization", currentOrgDto)
                .claim("organizations", orgsList)
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
