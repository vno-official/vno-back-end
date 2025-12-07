package com.vno.auth.dto;

import java.util.UUID;

public class AuthResponse {
    public String token;
    public String refreshToken;

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

    public static AuthResponse create(String token, String refreshToken) {
        AuthResponse response = new AuthResponse();
        response.token = token;
        response.refreshToken = refreshToken;
        return response;
    }
}
