package com.cst438.project02.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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


    @Column(name = "password", nullable = true, length = 100)
    private String passwordHash;

    @Column(unique = true)
    private String email;

    @Column(nullable = true)
    private String name;

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = password;
    }
}
