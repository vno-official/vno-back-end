package com.vno.org.web;

import com.vno.core.dto.OrganizationDto;
import com.vno.core.entity.Organization;
import com.vno.core.tenant.TenantContext;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/org")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Organization", description = "Organization management endpoints")
public class OrgResource {

    @GET
    public Uni<OrganizationDto> getCurrentOrg() {
        UUID orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            // Should be handled by filter or throw exception, but here we fail nicely
            return Uni.createFrom().failure(new BadRequestException("No organization context"));
        }

        return Organization.<Organization>findById(orgId)
            .onItem().ifNull().failWith(new NotFoundException("Organization not found"))
            .map(org -> new OrganizationDto(org.id, org.name, org.slug, org.plan, org.createdAt != null ? org.createdAt.toString() : null));
    }
}
