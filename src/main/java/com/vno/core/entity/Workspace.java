package com.vno.core.entity;

import com.vno.core.tenant.TenantEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Workspace extends TenantEntity {

    @Column(nullable = false)
    public String name;

    @Column(name = "icon_emoji")
    public String iconEmoji;

    @Column(name = "cover_url")
    public String coverUrl;

    @Column(name = "default_permission")
    public String defaultPermission = "organization";

    @Column(name = "is_system")
    public Boolean isSystem = false;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public User createdBy;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    public Instant deletedAt;

    // Reactive repository methods
    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<Workspace> findByIdAndOrg(UUID id, UUID orgId) {
        return find("id = ?1 and organizationId = ?2 and deletedAt is null", id, orgId).firstResult();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<Workspace> findPrivateWorkspace(UUID orgId, UUID userId) {
        return find("organizationId = ?1 and createdBy.id = ?2 and isSystem = true and name = 'Private' and deletedAt is null", 
                    orgId, userId).firstResult();
    }

    public boolean isDeletable() {
        return !Boolean.TRUE.equals(isSystem);
    }
}
