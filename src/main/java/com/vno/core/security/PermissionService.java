package com.vno.core.security;

import java.util.UUID;

import com.vno.core.entity.Role;
import com.vno.core.entity.Workspace;
import com.vno.core.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class PermissionService {

    /**
     * Check if user has access to a workspace
     * - Private workspaces: only owner can access
     * - Organization workspaces: all members can access
     */
    public boolean canAccessWorkspace(Workspace workspace, Long userId, Role userRole) {
        if (workspace == null) {
            return false;
        }

        // Check tenant isolation
        UUID currentOrgId = TenantContext.getOrganizationId();
        if (currentOrgId == null || !currentOrgId.equals(workspace.organizationId)) {
            return false;
        }

        // Private workspace: only owner can access
        if ("private".equals(workspace.defaultPermission)) {
            return workspace.createdBy != null && workspace.createdBy.id.equals(userId);
        }

        // Organization workspace: all members can access
        return true;
    }

    /**
     * Check if user has required role
     */
    public boolean hasRole(Role userRole, Role requiredRole) {
        if (userRole == null || requiredRole == null) {
            return false;
        }

        // OWNER has all permissions
        if (userRole == Role.OWNER) {
            return true;
        }

        // ADMIN has ADMIN and MEMBER permissions
        if (userRole == Role.ADMIN) {
            return requiredRole == Role.ADMIN || requiredRole == Role.MEMBER;
        }

        // MEMBER only has MEMBER permissions
        return userRole == requiredRole;
    }

    /**
     * Require user to have access to workspace, throw exception if not
     */
    public void requireWorkspaceAccess(Workspace workspace, Long userId, Role userRole) {
        if (!canAccessWorkspace(workspace, userId, userRole)) {
            throw new ForbiddenException("You don't have permission to access this workspace");
        }
    }

    /**
     * Require user to have specific role, throw exception if not
     */
    public void requireRole(Role userRole, Role requiredRole) {
        if (!hasRole(userRole, requiredRole)) {
            throw new ForbiddenException("You don't have permission to perform this action");
        }
    }

    /**
     * Check if workspace can be deleted (system workspaces cannot be deleted)
     */
    public boolean canDeleteWorkspace(Workspace workspace, Role userRole) {
        if (workspace == null) {
            return false;
        }

        // System workspaces (Private) cannot be deleted
        if (!workspace.isDeletable()) {
            return false;
        }

        // Only OWNER and ADMIN can delete workspaces
        return userRole == Role.OWNER || userRole == Role.ADMIN;
    }
}
