package com.vno.auth.dto;

import java.util.UUID;

public class AuthResponse {
    public String token;
    public UserInfo user;
    public OrgInfo organization;

    public static class UserInfo {
        public UUID id;
        public String email;
        public String name;
        public String avatarUrl;
    }

    public static class OrgInfo {
        public UUID id;
        public String slug;
        public String name;
    }

    public static AuthResponse create(String token, com.vno.core.entity.User user, com.vno.core.entity.Organization org) {
        AuthResponse response = new AuthResponse();
        response.token = token;
        
        response.user = new UserInfo();
        response.user.id = user.id;
        response.user.email = user.email;
        response.user.name = user.name;
        response.user.avatarUrl = user.avatarUrl;
        
        response.organization = new OrgInfo();
        response.organization.id = org.id;
        response.organization.slug = org.slug;
        response.organization.name = org.name;
        
        return response;
    }
}
