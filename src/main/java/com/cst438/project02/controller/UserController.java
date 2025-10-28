package com.cst438.project02.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.project02.entity.User;
import com.cst438.project02.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
    
    @PostMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent() || !existingUser.get().getPasswordHash().equals(user.getPasswordHash())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Invalid username or password!");
        }
        return ResponseEntity.ok("User logged in successfully!");
    }
    
    @PostMapping("id")
    public ResponseEntity<?> getUserId(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: User not found!");
        }
        return ResponseEntity.ok(existingUser.get().getId());
    }
    
}