package com.vno.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class UserProfile extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    public String email;

    @NotBlank
    @Column(nullable = false)
    public String name;

    public String phone;

    public boolean archived = false;
}
