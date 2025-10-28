package com.cst438.project02.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.project02.entity.User;
import com.cst438.project02.repository.UserRepository;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: User not found with id " + id);
        }
        return ResponseEntity.ok(user.get());
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: User not found with username " + username);
        }
        return ResponseEntity.ok(user.get());
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: User not found with email " + email);
        }
        return ResponseEntity.ok(user.get());
    }

    // Update user
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateRequest) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: User not found with id " + id);
        }

        User existingUser = existingUserOpt.get();

        try {
            // Update username if provided and not taken by another user
            if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(existingUser.getUsername())) {
                if (userRepository.existsByUsername(updateRequest.getUsername())) {
                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body("Error: Username is already taken");
                }
                existingUser.setUsername(updateRequest.getUsername());
            }

            // Update email if provided and not taken by another user
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(updateRequest.getEmail())) {
                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body("Error: Email is already taken");
                }
                existingUser.setEmail(updateRequest.getEmail());
            }

            // Update name if provided
            if (updateRequest.getName() != null) {
                existingUser.setName(updateRequest.getName());
            }

            // Update password if provided (hash it)
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(updateRequest.getPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    // Delete user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: User not found with id " + id);
        }

        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    // Legacy endpoints (kept for backward compatibility)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (!existingUser.isPresent() || !existingUser.get().getPasswordHash().equals(user.getPasswordHash())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Invalid username or password!");
        }
        return ResponseEntity.ok("User logged in successfully!");
    }

    @PostMapping("/id")
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

class UserUpdateRequest {
    private String username;
    private String email;
    private String name;
    private String password;

    public UserUpdateRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}