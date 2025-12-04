package com.vno.core.entity;

import com.vno.core.tenant.TenantEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "blocks")
public class Block extends TenantEntity {
    // organizationId is inherited from TenantEntity - DO NOT duplicate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    public Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_block_id")
    public Block parentBlock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BlockType type;

    @Column(columnDefinition = "jsonb", nullable = false)
    public String content = "{}";

    @Column(name = "order_index", nullable = false)
    public Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    public User createdBy;

    @Column(name = "created_at")
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt = Instant.now();

    public enum BlockType {
        TEXT,
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
    public static Uni<List<Block>> findByPage(Long pageId) {
        return list("page.id = ?1 order by orderIndex", pageId);
    }

    public static Uni<Void> deleteByPage(Long pageId) {
        return delete("page.id", pageId).replaceWithVoid();
    }

    public static Uni<Integer> getMaxOrderIndex(Long pageId) {
        return find("select max(orderIndex) from Block where page.id = ?1", pageId)
            .project(Integer.class)
            .singleResult()
            .onItem().ifNull().continueWith(0);
    }
}
