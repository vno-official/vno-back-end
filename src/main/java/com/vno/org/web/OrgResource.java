package com.vno.org.web;

import com.vno.core.entity.Organization;
import com.vno.core.tenant.TenantContext;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/org")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class OrgResource {

    @GET
    public Uni<Response> getCurrentOrg() {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        return Organization.<Organization>findById(orgId)
            .onItem().ifNull().failWith(new NotFoundException("Organization not found"))
            .map(org -> Response.ok(org).build());
    }
}
