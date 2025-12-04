package com.vno.auth.web;

import com.vno.auth.dto.LoginRequest;
import com.vno.auth.dto.RegisterRequest;
import com.vno.auth.service.AuthService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    /**
     * Register new user with username/password
     */
    @POST
    @Path("/register")
    public Uni<Response> register(@Valid RegisterRequest request) {
        return authService.registerUser(request.email, request.password, request.name)
            .map(token -> Response.ok()
                .entity(new JsonObject()
                    .put("token", token)
                    .put("message", "Registration successful"))
                .build());
    }

    /**
     * Login with username/password
     */
    @POST
    @Path("/login")
    public Uni<Response> login(@Valid LoginRequest request) {
        return authService.loginUser(request.email, request.password)
            .map(token -> Response.ok()
                .entity(new JsonObject()
                    .put("token", token))
                .build());
    }

    /**
     * Switch to different organization - returns new JWT
     */
    @POST
    @Path("/switch-org")
    @Authenticated
    public Uni<Response> switchOrganization(@QueryParam("orgId") Long orgId) {
        if (orgId == null) {
            throw new BadRequestException("orgId is required");
        }

        Long userId = Long.parseLong(jwt.getSubject());
        return authService.switchOrganization(userId, orgId)
            .map(token -> Response.ok()
                .entity(new JsonObject()
                    .put("token", token))
                .build());
    }

    /**
     * Get current user info (for testing)
     */
    @GET
    @Path("/me")
    @Authenticated
    public Response getCurrentUser() {
        Long userId = Long.parseLong(jwt.getSubject());
        String email = jwt.getClaim("email");
        Long currentOrgId = jwt.getClaim("org_id");
        String currentRole = jwt.getClaim("role");

        return Response.ok()
            .entity(new JsonObject()
                .put("user_id", userId)
                .put("email", email)
                .put("current_org_id", currentOrgId)
                .put("current_role", currentRole))
            .build();
    }
}
