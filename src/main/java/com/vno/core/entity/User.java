package com.vno.core.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "password_hash")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String passwordHash;

    @Column
    public String name;

    @Column(name = "avatar_url")
    public String avatarUrl;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    // Reactive repository methods
    public static Uni<User> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static Uni<User> findOrCreate(String email, String name, String avatarUrl) {
        return findByEmail(email)
            .onItem().ifNull().switchTo(() -> {
                User user = new User();
                user.email = email;
                user.name = name;
                user.avatarUrl = avatarUrl;
                return user.persistAndFlush().replaceWith(user);
            })
            .onItem().ifNotNull().transform(user -> {
                if (name != null && !name.isEmpty()) user.name = name;
                if (avatarUrl != null && !avatarUrl.isEmpty()) user.avatarUrl = avatarUrl;
                return user;
            });
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Uni<List<Organization>> getOrganizations() {
        return UserOrganization.<UserOrganization>find(
            "SELECT uo FROM UserOrganization uo " +
            "JOIN FETCH uo.organization " +
            "WHERE uo.user.id = ?1", this.id
        ).list()
            .map(list -> list.stream()
                .map(uo -> uo.organization)
                .toList());
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Uni<Boolean> hasAnyOrganization() {
        return UserOrganization.count("user.id", this.id)
            .map(count -> count > 0);
    }

    // Password helpers
    @com.fasterxml.jackson.annotation.JsonIgnore
    public void setPassword(String plainPassword) {
        this.passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean verifyPassword(String plainPassword) {
        if (this.passwordHash == null || plainPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, this.passwordHash);
    }
}
