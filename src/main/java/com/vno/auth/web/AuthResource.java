package com.vno.auth.web;

import com.vno.auth.dto.ChangePasswordRequest;
import com.vno.auth.dto.LoginRequest;
import com.vno.auth.dto.RefreshTokenRequest;
import com.vno.auth.dto.RegisterRequest;
import com.vno.auth.dto.RequestPasswordResetRequest;
import com.vno.auth.dto.ResetPasswordRequest;
import com.vno.auth.service.AuthService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
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
            .map(authTokens -> Response.ok(authTokens).build());
    }

    /**
     * Login with username/password
     */
    @POST
    @Path("/login")
    public Uni<Response> login(@Valid LoginRequest request) {
        return authService.loginUser(request.email, request.password)
            .map(authTokens -> Response.ok(authTokens).build());
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
            .map(token -> Response.ok(new com.vno.core.dto.TokenDto(token)).build());
    }

    /**
     * Get current user info (for testing)
     */
    @GET
    @Path("/me")
    @Authenticated
    @SecurityRequirement(name = "bearerAuth")
    public Response getCurrentUser() {
        String userIdStr = jwt.getSubject();
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        
        String orgIdStr = jwt.getClaim("org_id");
        UUID currentOrgId = orgIdStr != null ? UUID.fromString(orgIdStr) : null;
        
        String currentRole = jwt.getClaim("role");

        return Response.ok(new com.vno.core.dto.UserContextDto(userId, email, name, currentOrgId, currentRole)).build();
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
            .map(v -> Response.ok(new com.vno.core.dto.MessageDto("Password changed successfully")).build());
    }

    /**
     * Request password reset email
     */
    @POST
    @Path("/request-reset")
    public Uni<Response> requestPasswordReset(@Valid RequestPasswordResetRequest request) {
        return authService.requestPasswordReset(request.email)
            .map(v -> Response.ok(new com.vno.core.dto.MessageDto("If the email exists, a password reset link has been sent")).build());
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
            .map(v -> Response.ok(new com.vno.core.dto.MessageDto("Password reset successfully")).build());
    }

    /**
     * Refresh access token using refresh token
     */
    @POST
    @Path("/refresh")
    public Uni<Response> refreshToken(@Valid RefreshTokenRequest request) {
        return authService.refreshAccessToken(request.refreshToken)
            .map(accessToken -> Response.ok(
                new com.vno.core.dto.AccessTokenDto(accessToken, 900)
            ).build());
    }

    /**
     * Revoke refresh token (logout)
     */
    @POST
    @Path("/revoke")
    public Uni<Response> revokeToken(@Valid RefreshTokenRequest request) {
        return authService.revokeRefreshToken(request.refreshToken)
            .map(v -> Response.ok(new com.vno.core.dto.MessageDto("Token revoked successfully")).build());
    }
}
