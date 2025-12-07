package com.vno.core.tenant;

import java.util.UUID;

public class TenantContext {
    
    private static final ThreadLocal<UUID> currentOrganizationId = new ThreadLocal<>();
    
    public static void setOrganizationId(UUID orgId) {
        currentOrganizationId.set(orgId);
    }
    
    public static UUID getOrganizationId() {
        System.out.println(currentOrganizationId.get());
        return currentOrganizationId.get();
    }
    
    public static void clear() {
        currentOrganizationId.remove();
    }
}
