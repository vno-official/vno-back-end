package com.vno.core.tenant;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

@MappedSuperclass
@FilterDef(name = "organizationFilter", parameters = @ParamDef(name = "organizationId", type = UUID.class))
@Filter(name = "organizationFilter", condition = "organization_id = :organizationId")
public abstract class TenantEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    public UUID id;

    @Column(name = "organization_id", nullable = false, columnDefinition = "uuid")
    public UUID organizationId;

    @PrePersist
    public void setOrganizationIdFromContext() {
        System.out.println("123123 + " + this.toString());
        if (this.organizationId == null) {
            this.organizationId = TenantContext.getOrganizationId();
        }
    }
}
