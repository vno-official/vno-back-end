package com.vno.core.tenant;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION + 1) // Run after authentication
public class TenantFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip tenant resolution for auth endpoints and health checks
        if (path.startsWith("auth/") || path.startsWith("q/") || path.startsWith("api/health") || path.startsWith("api/auth")) {
            return;
        }

        System.out.println("🟡 TenantFilter: SecurityIdentity anonymous? " + securityIdentity.isAnonymous());
        System.out.println("🟡 TenantFilter: Principal: " + securityIdentity.getPrincipal());
        
        // Extract org_id from JWT claims via SecurityIdentity
        if (!securityIdentity.isAnonymous()) {
            // Try to get JsonWebToken from principal
            if (securityIdentity.getPrincipal() instanceof JsonWebToken) {
                JsonWebToken jwt = (JsonWebToken) securityIdentity.getPrincipal();
                
                if (jwt.claim("org_id").isPresent()) {
                    Object orgIdClaim = jwt.claim("org_id").get();
                    Long orgId = orgIdClaim instanceof Number 
                        ? ((Number) orgIdClaim).longValue() 
                        : Long.parseLong(orgIdClaim.toString());
                    
                    System.out.println("🔵 TenantFilter: Extracted org_id from JWT: " + orgId);
                    TenantContext.setOrganizationId(orgId);
                    System.out.println("🔵 TenantFilter: Set TenantContext.organizationId to: " + TenantContext.getOrganizationId());
                } else {
                    System.out.println("� TenantFilter: org_id claim not present in JWT");
                }
            } else {
                // Try to get from attributes (for OIDC tokens)
                Object orgIdAttr = securityIdentity.getAttribute("org_id");
                if (orgIdAttr != null) {
                    Long orgId = orgIdAttr instanceof Number 
                        ? ((Number) orgIdAttr).longValue() 
                        : Long.parseLong(orgIdAttr.toString());
                    
                    System.out.println("🔵 TenantFilter: Extracted org_id from attributes: " + orgId);
                    TenantContext.setOrganizationId(orgId);
                } else {
                    System.out.println("� TenantFilter: Principal is not JsonWebToken and no org_id attribute. Type: " + securityIdentity.getPrincipal().getClass().getName());
                }
            }
        } else {
            System.out.println("🔴 TenantFilter: SecurityIdentity is anonymous");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Clear tenant context after request completes
        TenantContext.clear();
    }
}
