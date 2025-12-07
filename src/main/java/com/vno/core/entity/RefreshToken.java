package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false, unique = true)
    public String token;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Column(name = "revoked_at")
    public Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "last_used_at")
    public Instant lastUsedAt;

    /**
     * Check if the token has expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Check if the token has been revoked
     */
    public boolean isRevoked() {
        return revokedAt != null;
    }

    /**
     * Revoke the token
     */
    public void revoke() {
        this.revokedAt = Instant.now();
    }

    /**
     * Update last used timestamp
     */
    public void updateLastUsed() {
        this.lastUsedAt = Instant.now();
    }

    /**
     * Find a valid (not expired, not revoked) refresh token
     * 
     * @param token The token string to search for
     * @return Uni with the token if found and valid, null otherwise
     */
    public static Uni<RefreshToken> findValidToken(String token) {
        return find("token = ?1 AND expiresAt > ?2 AND revokedAt IS NULL", 
                    token, Instant.now())
            .firstResult();
    }

    /**
     * Find a refresh token by token string (regardless of validity)
     * 
     * @param token The token string to search for
     * @return Uni with the token if found, null otherwise
     */
    public static Uni<RefreshToken> findByToken(String token) {
        return find("token", token).firstResult();
    }

    /**
     * Find all active (non-revoked, non-expired) tokens for a user
     * 
     * @param userId The user ID
     * @return Uni with list of active tokens
     */
    public static Uni<Long> countActiveTokensForUser(UUID userId) {
        return count("user.id = ?1 AND expiresAt > ?2 AND revokedAt IS NULL", 
                    userId, Instant.now());
    }
}
