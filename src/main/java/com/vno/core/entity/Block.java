package com.vno.core.entity;

import com.vno.core.tenant.TenantEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "blocks")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Block extends TenantEntity {
    // organizationId is inherited from TenantEntity - DO NOT duplicate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_block_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Block parentBlock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BlockType type;

@JdbcTypeCode(SqlTypes.JSON)
    public String content = "{}";

    @Column(name = "order_index", nullable = false)
    public Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public User createdBy;

    @Column(name = "created_at")
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt = Instant.now();

    public enum BlockType {
        TEXT,
        HEADING,       // Generic heading with level in content JSON
        HEADING_1,
        HEADING_2,
        HEADING_3,
        TODO,
        TOGGLE,
        BULLET_LIST,
        NUMBERED_LIST,
        CALLOUT,
        DIVIDER,
        CODE
    }

    // Reactive finder methods
    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<List<Block>> findByPage(UUID pageId) {
        return list("page.id = ?1 order by orderIndex", pageId);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<Void> deleteByPage(UUID pageId) {
        return delete("page.id", pageId).replaceWithVoid();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public static Uni<Integer> getMaxOrderIndex(UUID pageId) {
        return find("select max(orderIndex) from Block where page.id = ?1", pageId)
            .project(Integer.class)
            .singleResult()
            .onItem().ifNull().continueWith(0);
    }
}
