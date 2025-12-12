package com.vno.core.dto;

import java.util.UUID;

public class OrganizationDto {
    public UUID id;
    public String name;
    public String slug;
    public String plan;
    public String createdAt;

    public OrganizationDto() {}

    public OrganizationDto(UUID id, String name, String slug, String plan, String createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.plan = plan;
        this.createdAt = createdAt;
    }
}
