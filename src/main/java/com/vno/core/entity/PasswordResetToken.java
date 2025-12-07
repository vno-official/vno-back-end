package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends PanacheEntityBase {

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

    @Column(name = "used_at")
    public Instant usedAt;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    /**
     * Check if the token has expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Check if the token has been used
     */
    public boolean isUsed() {
        return usedAt != null;
    }

    /**
     * Mark the token as used
     */
    public void markAsUsed() {
        this.usedAt = Instant.now();
    }

    /**
     * Find a valid (not expired, not used) token
     * 
     * @param token The token string to search for
     * @return Uni with the token if found and valid, null otherwise
     */
    public static Uni<PasswordResetToken> findValidToken(String token) {
        return find("token = ?1 AND expiresAt > ?2 AND usedAt IS NULL", 
                    token, Instant.now())
            .firstResult();
    }

    /**
     * Find a token by token string (regardless of validity)
     * 
     * @param token The token string to search for
     * @return Uni with the token if found, null otherwise
     */
    public static Uni<PasswordResetToken> findByToken(String token) {
        return find("token", token).firstResult();
    }
}
