package com.cst438.project02.auth.controller;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.dto.RegisterRequest;
import com.cst438.project02.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

// @RestController used for all controllers
// @RequestMapping sets the base path for our class
// @RequiredArgsConstructor generates a constructor for all required arguments like AuthService
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/google")
    // Use RequestBody to take the JSON in the HTTP body
    // @Valid confirms the Bean validation (NotBlank) idToken from GoogleLoginRequest
    public AuthResponse googleLogin(@RequestBody @Valid GoogleLoginRequest request)
            throws GeneralSecurityException, IOException {
        if (request.getAuthorizationCode() != null) {
            return authService.loginWithGoogleCode(request.getAuthorizationCode(), request.getCodeVerifier());
        }
        return authService.loginWithGoogle(request); // uses idToken
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam String code,
                                                 @RequestParam(required = false) String state) throws Exception {
        authService.loginWithGoogleCode(code, null);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }
}
