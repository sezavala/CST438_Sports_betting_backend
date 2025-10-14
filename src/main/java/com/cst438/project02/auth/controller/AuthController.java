package com.cst438.project02.auth.controller;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public AuthResponse googleLoginRequest(@RequestBody @Valid GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        return authService.loginWithGoogle(request);
    }
}
