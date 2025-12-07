package com.vno.workspace.web;

import com.vno.core.entity.Page;
import com.vno.core.entity.User;
import com.vno.core.entity.Workspace;
import com.vno.core.tenant.TenantContext;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/pages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Page", description = "Page management endpoints")
public class PageResource {

    @GET
    public Uni<Response> listPages(@QueryParam("workspaceId") Long workspaceId) {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        if (workspaceId != null) {
            return Page.<Page>list("organizationId = ?1 and workspace.id = ?2 and deletedAt is null", orgId, workspaceId)
                .map(pages -> Response.ok(pages).build());
        } else {
            return Page.<Page>list("organizationId = ?1 and deletedAt is null", orgId)
                .map(pages -> Response.ok(pages).build());
        }
    }

    @POST
    @WithTransaction
    public Uni<Response> createPage(PageCreateRequest request, @Context SecurityContext securityContext) {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        String userId = securityContext.getUserPrincipal().getName();

        return Workspace.findByIdAndOrg(request.workspaceId, orgId)
            .onItem().ifNull().failWith(new NotFoundException("Workspace not found"))
            .flatMap(workspace -> User.<User>findById(Long.parseLong(userId))
                .flatMap(user -> {
                    Page page = new Page();
                    page.organizationId = orgId;
                    page.workspace = workspace;
                    page.title = request.title != null ? request.title : "Untitled";
                    page.iconEmoji = request.iconEmoji;
                    page.createdBy = user;

                    if (request.parentPageId != null) {
                        return Page.findByIdAndOrg(request.parentPageId, orgId)
                            .flatMap(parentPage -> {
                                if (parentPage != null) {
                                    page.parentPage = parentPage;
                                }
                                return page.persistAndFlush();
                            });
                    } else {
                        return page.persistAndFlush();
                    }
                })
            )
            .map(page -> Response.status(Response.Status.CREATED).entity(page).build());
    }

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> deletePage(@PathParam("id") Long id) {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        return Page.findByIdAndOrg(id, orgId)
            .onItem().ifNull().failWith(new NotFoundException("Page not found"))
            .flatMap(page -> {
                page.softDelete();
                return page.persistAndFlush();
            })
            .map(page -> Response.ok("{\"message\":\"Page deleted\"}").build());
    }

    public static class PageCreateRequest {
        public Long workspaceId;
        public String title;
        public String iconEmoji;
        public Long parentPageId;
    }
}
