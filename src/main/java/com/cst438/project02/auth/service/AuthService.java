package com.cst438.project02.auth.service;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.dto.GoogleUserInfo;
import com.cst438.project02.auth.dto.UserView;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;
import com.cst438.project02.auth.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;
    // Method that verifies the login request token, creates a user, and returns the authentication response
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        GoogleUserInfo userInfo = googleTokenVerifier.verify(request.getIdToken());

        String jwtToken = jwtUtil.generateToken(userInfo.getEmail());

        UserView user = new UserView();
        user.setEmail(userInfo.getEmail());
        user.setId(userInfo.getId());
        user.setName(userInfo.getName());

        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtToken);
        response.setExpiresIn(jwtUtil.getJwtExpirationMs());
        response.setUser(user);
        return response;
    }
}
