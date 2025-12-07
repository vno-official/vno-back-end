package com.vno.auth.dto;

import com.vno.core.entity.Organization;
import com.vno.core.entity.User;

/**
 * Authentication response matching standard OAuth2 format.
 */
public class AuthTokens {
    public String accessToken;
    
    public String refreshToken;
    
    public int expiresIn = 900; // 15 minutes in seconds
    
    public UserInfo user;
    public TenantInfo tenant;
    public OrganizationInfo organization;

    public AuthTokens(String accessToken, String refreshToken, User user, Organization org) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        
        // User info
        this.user = new UserInfo();
        this.user.id = user.id;
        this.user.name = user.name;
        this.user.email = user.email;
        this.user.avatarUrl = user.avatarUrl;
        
        // Tenant info (main organization)
        this.tenant = new TenantInfo();
        this.tenant.id = org.id;
        this.tenant.name = org.name;
        this.tenant.plan = org.plan != null ? org.plan : "free";
        
        // Organization info (currently same as tenant, can be extended for branches)
        this.organization = new OrganizationInfo();
        this.organization.id = org.id;
        this.organization.name = org.name;
        this.organization.code = org.slug.toUpperCase();
        this.organization.role = "OWNER"; // This will be fetched from UserOrganization in future enhancement
        this.organization.permissions = new String[]{"*"}; // Full permissions for owner
    }

    public static class UserInfo {
        public java.util.UUID id;
        public String name;
        public String email;
        public String avatarUrl;
    }

    public static class TenantInfo {
        public java.util.UUID id;
        public String name;
        public String plan;
    }

    public static class OrganizationInfo {
        public java.util.UUID id;
        public String name;
        public String code;
        public String role;
        public String[] permissions;
    }
}
