package com.vno.web;

import com.vno.core.dto.UserContextDto;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/whoami")
@Tag(name = "Utility", description = "Utility endpoints for health checks and diagnostics")
public class WhoAmIResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @SecurityRequirement(name = "bearerAuth")
    public UserContextDto getMe(@Context SecurityContext securityContext) {
        String userIdStr = jwt.getSubject();
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        
        // Handle org_id which is stored as String in JWT but we treat as UUID
        String orgIdStr = jwt.getClaim("org_id");
        UUID orgId = orgIdStr != null ? UUID.fromString(orgIdStr) : null;
        
        String role = jwt.getClaim("role");

        return new UserContextDto(userId, email, name, orgId, role);
    }
}
