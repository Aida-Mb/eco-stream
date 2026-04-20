package com.ecostream.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;  // stocké hashé (BCrypt)

    @Column(nullable = false, length = 20)
    private String role;      // ex: "ROLE_USER", "ROLE_ADMIN"
}