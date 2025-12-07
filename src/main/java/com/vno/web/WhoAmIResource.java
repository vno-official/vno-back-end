package com.vno.web;

import com.vno.util.SubdomainUtil;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/whoami")
@Tag(name = "Utility", description = "Utility endpoints for health checks and diagnostics")
public class WhoAmIResource {

    @Inject
    SubdomainUtil subdomainUtil;

    @Inject
    JsonWebToken jwt;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response whoAmI(@Context HttpHeaders headers) {
        String host = headers.getHeaderString("Host");
        String org = subdomainUtil.extractOrgSlug(host);
        String json = String.format("{\"host\":\"%s\",\"orgSlug\":%s}",
                host == null ? "" : host,
                org == null ? "null" : ("\"" + org + "\""));
        return Response.ok(json).build();
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    @SecurityRequirement(name = "bearerAuth")
    public Response getMe(@Context SecurityContext securityContext) {
        String userId = jwt.getSubject();
        String email = jwt.getClaim("email");
        String name = jwt.getClaim("name");
        Long orgId = jwt.getClaim("org_id");
        String role = jwt.getClaim("role");

        String json = String.format(
            "{\"id\":%s,\"email\":\"%s\",\"name\":\"%s\",\"orgId\":%d,\"role\":\"%s\"}",
            userId, email, name != null ? name : "", orgId, role
        );
        return Response.ok(json).build();
    }
}
