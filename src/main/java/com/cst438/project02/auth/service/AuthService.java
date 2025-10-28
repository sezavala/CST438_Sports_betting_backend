package com.cst438.project02.auth.service;

import com.cst438.project02.auth.config.JwtUtil;
import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.dto.GoogleUserInfo;
import com.cst438.project02.auth.dto.LoginRequest;
import com.cst438.project02.auth.dto.RegisterRequest;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;
import com.cst438.project02.entity.User;
import com.cst438.project02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return AuthResponse.fromUser(user, token, jwtUtil.getExpirationTime());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return AuthResponse.fromUser(user, token, jwtUtil.getExpirationTime());
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request)
            throws GeneralSecurityException, IOException {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(request.getIdToken());

        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .name(googleUser.getName())
                            .username(googleUser.getEmail().split("@")[0])
                            .googleId(googleUser.getSub())
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user);
        return AuthResponse.fromUser(user, token, jwtUtil.getExpirationTime());
    }
}