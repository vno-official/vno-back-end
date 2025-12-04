package com.vno.workspace.web;

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

@Path("/api/workspaces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WorkspaceResource {

    @GET
    public Uni<Response> listWorkspaces() {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        return Workspace.<Workspace>list("organizationId = ?1 and deletedAt is null", orgId)
            .map(workspaces -> Response.ok(workspaces).build());
    }

    @POST
    @WithTransaction
    public Uni<Response> createWorkspace(WorkspaceCreateRequest request, @Context SecurityContext securityContext) {
        Long orgId = TenantContext.getOrganizationId();
        if (orgId == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"No organization context\"}")
                .build());
        }

        String userId = securityContext.getUserPrincipal().getName();

        return User.<User>findById(Long.parseLong(userId))
            .flatMap(user -> {
                Workspace workspace = new Workspace();
                workspace.organizationId = orgId;
                workspace.name = request.name;
                workspace.iconEmoji = request.iconEmoji;
                workspace.createdBy = user;
                return workspace.persistAndFlush();
            })
            .map(workspace -> Response.status(Response.Status.CREATED).entity(workspace).build());
    }

    public static class WorkspaceCreateRequest {
        public String name;
        public String iconEmoji;
    }
}
