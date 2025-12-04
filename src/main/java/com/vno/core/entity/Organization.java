package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String slug;

    @Column(name = "plan")
    public String plan = "free";

    @Column(name = "stripe_customer_id")
    public String stripeCustomerId;

    @Column(name = "trial_ends_at")
    public Instant trialEndsAt;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "deleted_at")
    public Instant deletedAt;

    // Reactive repository methods
    public static Uni<Organization> findBySlug(String slug) {
        return find("slug = ?1 and deletedAt is null", slug).firstResult();
    }

    public static Uni<Boolean> slugExists(String slug) {
        return count("slug = ?1 and deletedAt is null", slug)
            .map(count -> count > 0);
    }

    // Multi-org support methods (reactive)
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Uni<Role> getUserRole(Long userId) {
        return UserOrganization.findRoleByUserAndOrg(userId, this.id);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Uni<List<UserOrganization>> getMembers() {
        return UserOrganization.<UserOrganization>find(
            "SELECT uo FROM UserOrganization uo " +
            "JOIN FETCH uo.user " +
            "WHERE uo.organization.id = ?1", this.id
        ).list();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Uni<Boolean> hasUser(Long userId) {
        return UserOrganization.userBelongsToOrg(userId, this.id);
    }
}
