package com.vno.auth.web;

import com.vno.auth.dto.ChangePasswordRequest;
import com.vno.auth.dto.LoginRequest;
import com.vno.auth.dto.RegisterRequest;
import com.vno.auth.dto.RequestPasswordResetRequest;
import com.vno.auth.dto.ResetPasswordRequest;
import com.vno.auth.service.AuthService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
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
    @SecurityRequirement(name = "bearerAuth")
    public Uni<Response> switchOrganization(@QueryParam("orgId") UUID orgId) {
        if (orgId == null) {
            throw new BadRequestException("orgId is required");
        }

        UUID userId = UUID.fromString(jwt.getSubject());
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
    @SecurityRequirement(name = "bearerAuth")
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

    /**
     * Change password for authenticated user
     */
    @POST
    @Path("/change-password")
    @Authenticated
    @SecurityRequirement(name = "bearerAuth")
    public Uni<Response> changePassword(@Valid ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.passwordsMatch()) {
            throw new BadRequestException("New password and confirmation do not match");
        }

        // Validate password is actually different
        if (!request.passwordChanged()) {
            throw new BadRequestException("New password must be different from current password");
        }

        UUID userId = UUID.fromString(jwt.getSubject());
        return authService.changePassword(userId, request.currentPassword, request.newPassword)
            .map(v -> Response.ok()
                .entity(new JsonObject()
                    .put("message", "Password changed successfully"))
                .build());
    }

    /**
     * Request password reset email
     */
    @POST
    @Path("/request-reset")
    public Uni<Response> requestPasswordReset(@Valid RequestPasswordResetRequest request) {
        return authService.requestPasswordReset(request.email)
            .map(v -> Response.ok()
                .entity(new JsonObject()
                    .put("message", "If the email exists, a password reset link has been sent"))
                .build());
    }

    /**
     * Reset password with token
     */
    @POST
    @Path("/reset-password")
    public Uni<Response> resetPassword(@Valid ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.passwordsMatch()) {
            throw new BadRequestException("New password and confirmation do not match");
        }

        return authService.resetPassword(request.token, request.newPassword)
            .map(v -> Response.ok()
                .entity(new JsonObject()
                    .put("message", "Password reset successfully"))
                .build());
    }
}
