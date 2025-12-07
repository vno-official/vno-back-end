package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_organizations")
public class UserOrganization extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    public UUID id;

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
    public static Uni<UserOrganization> findByUserAndOrg(UUID userId, UUID orgId) {
        return find("user.id = ?1 and organization.id = ?2", userId, orgId).firstResult();
    }

    public static Uni<Role> findRoleByUserAndOrg(UUID userId, UUID orgId) {
        return findByUserAndOrg(userId, orgId)
            .map(uo -> uo != null ? uo.role : null);
    }

    public static Uni<Boolean> userBelongsToOrg(UUID userId, UUID orgId) {
        return count("user.id = ?1 and organization.id = ?2", userId, orgId)
            .map(count -> count > 0);
    }
}
