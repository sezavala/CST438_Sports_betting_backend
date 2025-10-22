package com.cst438.project02.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // Map to existing NOT NULL column 'password'
    @Column(name = "password", nullable = false, length = 100)
    private String passwordHash;

    @Column(unique = true)
    private String email;

    private String name;
}