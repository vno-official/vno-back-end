package com.vno.core.dto;

import java.util.UUID;

public class UserContextDto {
    public UUID userId;
    public String email;
    public String name;
    public UUID currentOrgId;
    public String currentRole;

    public UserContextDto() {}

    public UserContextDto(UUID userId, String email, String name, UUID currentOrgId, String currentRole) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.currentOrgId = currentOrgId;
        this.currentRole = currentRole;
    }
}
