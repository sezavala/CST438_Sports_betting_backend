package com.cst438.project02.auth.service;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        return new AuthResponse();
    }
}
