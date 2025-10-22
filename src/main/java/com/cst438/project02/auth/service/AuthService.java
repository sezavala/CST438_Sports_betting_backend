package com.cst438.project02.auth.service;

import com.cst438.project02.auth.dto.*;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;
import com.cst438.project02.auth.config.JwtUtil;
import com.cst438.project02.entity.User;
import com.cst438.project02.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret; // optional when using PKCE

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri;

    private final RestClient restClient = RestClient.builder().build();

    public AuthResponse loginWithGoogle(GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        var userInfo = googleTokenVerifier.verify(request.getIdToken());
        return buildAuthResponse(userInfo);
    }

    public AuthResponse loginWithGoogleCode(String code, String codeVerifier) throws GeneralSecurityException, IOException {
        MultiValueMap<String, String> form = getStringStringMultiValueMap(code, codeVerifier);

        var token = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);

        var userInfo = googleTokenVerifier.verify(token.getIdToken());
        return buildAuthResponse(userInfo);
    }

    private MultiValueMap<String, String> getStringStringMultiValueMap(String code, String codeVerifier) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", redirectUri);

        // Use PKCE if codeVerifier provided, otherwise client secret (server-side web flow)
        if (codeVerifier != null && !codeVerifier.isBlank()) {
            form.add("code_verifier", codeVerifier);
        } else {
            form.add("client_secret", clientSecret);
        }
        return form;
    }

    private AuthResponse buildAuthResponse(GoogleUserInfo userInfo) {
        var user = new UserView();
        user.setId(userInfo.getId());
        user.setEmail(userInfo.getEmail());
        user.setName(userInfo.getName());

        String jwt = jwtUtil.generateToken(user.getEmail());

        var resp = new AuthResponse();
        resp.setUser(user);
        resp.setAccessToken(jwt);
        resp.setExpiresIn(jwtUtil.getJwtExpirationMs() / 1000);
        resp.setTokenType("Bearer");
        return resp;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);

        // Build response (reuse format used by Google login)
        UserView uv = new UserView();
        uv.setId(saved.getId().toString());
        uv.setEmail(saved.getUsername());
        uv.setName(saved.getUsername());

        String jwt = jwtUtil.generateToken(saved.getUsername());

        AuthResponse resp = new AuthResponse();
        resp.setUser(uv);
        resp.setAccessToken(jwt);
        resp.setExpiresIn(jwtUtil.getJwtExpirationMs() / 1000);
        resp.setTokenType("Bearer");
        return resp;
    }
}
