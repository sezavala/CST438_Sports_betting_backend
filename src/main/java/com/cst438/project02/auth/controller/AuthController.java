package com.cst438.project02.auth.controller;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.dto.LoginRequest;
import com.cst438.project02.auth.dto.RegisterRequest;
import com.cst438.project02.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request)
            throws GeneralSecurityException, IOException {
        return ResponseEntity.ok(authService.loginWithGoogle(request));
    }
}