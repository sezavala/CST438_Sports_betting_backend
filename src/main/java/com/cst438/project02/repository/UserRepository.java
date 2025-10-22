package com.cst438.project02.repository;

import com.cst438.project02.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}