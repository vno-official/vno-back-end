package com.vno.core.dto;

import java.time.Instant;
import java.util.UUID;

public class UserOrganizationDto {
    public UUID id;
    public UserDto user;
    public OrganizationDto organization;
    public String role;
    public Instant joinedAt;

    public UserOrganizationDto() {}

    public UserOrganizationDto(UUID id, UserDto user, OrganizationDto organization, String role, Instant joinedAt) {
        this.id = id;
        this.user = user;
        this.organization = organization;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
