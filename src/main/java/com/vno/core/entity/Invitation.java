package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invitations")
public class Invitation extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization organization;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String role = "MEMBER";

    @Column(nullable = false, unique = true)
    public String token;

    @ManyToOne
    @JoinColumn(name = "invited_by")
    public User invitedBy;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Column(name = "accepted_at")
    public Instant acceptedAt;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    // Reactive repository methods
    public static Uni<Invitation> findByToken(String token) {
        return find("token = ?1 and acceptedAt is null and expiresAt > ?2",
                token, Instant.now()).firstResult();
    }

    public static Uni<Void> deleteExpired() {
        return delete("expiresAt < ?1 and acceptedAt is null", Instant.now())
            .replaceWithVoid();
    }
}
