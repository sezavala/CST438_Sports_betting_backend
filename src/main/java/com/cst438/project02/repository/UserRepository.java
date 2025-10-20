package com.cst438.project02.repository;

import com.cst438.project02.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
