package com.vno.core.tenant;

public class TenantContext {
    
    private static final ThreadLocal<Long> currentOrganizationId = new ThreadLocal<>();
    
    public static void setOrganizationId(Long orgId) {
        currentOrganizationId.set(orgId);
    }
    
    public static Long getOrganizationId() {
        System.out.println(currentOrganizationId.get());
        return currentOrganizationId.get();
    }
    
    public static void clear() {
        currentOrganizationId.remove();
    }
}
