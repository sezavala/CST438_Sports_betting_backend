package com.cst438.project02.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import com.cst438.project02.repository.UserRepository;
import com.cst438.project02.entity.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("register") //for springboot to register a new user
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
    @PostMapping("login") //for springboot to login an existing user
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Invalid username or password!");
        }
        return ResponseEntity.ok("User logged in successfully!");
    }
    @PostMapping("id") //for springboot to get user id by username
    public ResponseEntity<?> getUserId(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: User not found!");
        }
        return ResponseEntity.ok(existingUser.getId());
    }
    
}
