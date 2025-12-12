package com.vno.core.dto;

import java.util.UUID;

public class UserDto {
    public UUID id;
    public String email;
    public String name;
    public String avatarUrl;
    public String createdAt;

    public UserDto() {}

    public UserDto(UUID id, String email, String name, String avatarUrl, String createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }
}
