package com.vno.auth.dto;

import com.vno.core.entity.Organization;
import com.vno.core.entity.User;

/**
 * Response containing both access and refresh tokens along with user and org info.
 */
public class AuthTokens {
    public String accessToken;
    public String refreshToken;
    public UserInfo user;
    public OrgInfo organization;

    public AuthTokens(String accessToken, String refreshToken, User user, Organization org) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        
        this.user = new UserInfo();
        this.user.id = user.id;
        this.user.email = user.email;
        this.user.name = user.name;
        this.user.avatarUrl = user.avatarUrl;
        
        this.organization = new OrgInfo();
        this.organization.id = org.id;
        this.organization.slug = org.slug;
        this.organization.name = org.name;
    }

    public static class UserInfo {
        public java.util.UUID id;
        public String email;
        public String name;
        public String avatarUrl;
    }

    public static class OrgInfo {
        public java.util.UUID id;
        public String slug;
        public String name;
    }
}
