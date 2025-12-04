package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_organizations")
public class UserOrganization extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Column(name = "joined_at")
    public Instant joinedAt = Instant.now();

    // Reactive finder methods
    public static Uni<UserOrganization> findByUserAndOrg(Long userId, Long orgId) {
        return find("user.id = ?1 and organization.id = ?2", userId, orgId).firstResult();
    }

    public static Uni<Role> findRoleByUserAndOrg(Long userId, Long orgId) {
        return findByUserAndOrg(userId, orgId)
            .map(uo -> uo != null ? uo.role : null);
    }

    public static Uni<Boolean> userBelongsToOrg(Long userId, Long orgId) {
        return count("user.id = ?1 and organization.id = ?2", userId, orgId)
            .map(count -> count > 0);
    }
}
