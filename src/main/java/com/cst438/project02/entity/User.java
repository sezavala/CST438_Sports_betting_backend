like this package com.cst438.project02.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(nullable = false, unique = true)
private String username;


@Column(nullable = false)
private String password;


public User() {}


public User(String username, String password) {
    this.username = username;
    this.password = password;
}
public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}
public String getUsername() {
    return username;
}
public void setUsername(String username) {
    this.username = username;
}
public String getPassword() {
    return password;
}
public void setPassword(String password) {
    this.password = password;
}

}

    // Map to existing column 'password' (nullable for OAuth-only users)
    @Column(name = "password", nullable = true, length = 100)
    private String passwordHash;

    @Column(unique = true)
    private String email;

    private String name;
}
