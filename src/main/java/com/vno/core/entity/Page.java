package com.vno.core.entity;

import com.vno.core.tenant.TenantEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pages")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Page extends TenantEntity {

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "parent_page_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Page parentPage;

    @Column(nullable = false)
    public String title = "Untitled";

    @Column(name = "icon_emoji")
    public String iconEmoji;

    @Column(name = "cover_url")
    public String coverUrl;

    @Column
    public String path;

    @Column
    public String visibility = "inherit";

    @Column(name = "is_locked")
    public Boolean isLocked = false;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public User createdBy;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    public Instant deletedAt;

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    // Reactive repository methods
    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<Page> findByIdAndOrg(UUID id, UUID orgId) {
        return find("id = ?1 and organizationId = ?2 and deletedAt is null", id, orgId).firstResult();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
