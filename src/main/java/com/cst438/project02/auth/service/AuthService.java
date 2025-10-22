package com.cst438.project02.auth.service;

import com.cst438.project02.auth.config.JwtUtil;
import com.cst438.project02.auth.dto.*;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;
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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret; // optional when using PKCE

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String redirectUri;

    private final RestClient restClient = RestClient.builder().build();

    // ---------- Local (non-OAuth) auth ----------

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return buildAuthResponse(user);
    }

    // ---------- Google OAuth flows (unchanged, now persisting users) ----------

    public AuthResponse loginWithGoogle(GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        var userInfo = googleTokenVerifier.verify(request.getIdToken());
        var saved = upsertUserFromGoogle(userInfo);
        return buildAuthResponse(saved);
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
        var saved = upsertUserFromGoogle(userInfo);
        return buildAuthResponse(saved);
    }

    private MultiValueMap<String, String> getStringStringMultiValueMap(String code, String codeVerifier) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", redirectUri);

        if (codeVerifier != null && !codeVerifier.isBlank()) {
            form.add("code_verifier", codeVerifier);
        } else {
            form.add("client_secret", clientSecret);
        }
        return form;
    }

    private User upsertUserFromGoogle(GoogleUserInfo userInfo) {
        String email = userInfo.getEmail();
        String name = userInfo.getName();

        // Prefer matching by email. Set username = email for Google users.
        return userRepository.findByEmail(email)
                .map(u -> {
                    boolean changed = false;
                    if (name != null && !name.equals(u.getName())) {
                        u.setName(name);
                        changed = true;
                    }
                    if (u.getUsername() == null || !u.getUsername().equals(email)) {
                        u.setUsername(email);
                        changed = true;
                    }
                    return changed ? userRepository.save(u) : u;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setUsername(email);
                    // passwordHash remains null for OAuth-only users
                    return userRepository.save(u);
                });
    }

    private AuthResponse buildAuthResponse(User saved) {
        var uv = new UserView();
        uv.setId(saved.getId().toString());
        // Keep compatibility with existing UserView fields:
        uv.setEmail(saved.getEmail() != null ? saved.getEmail() : saved.getUsername());
        uv.setName(saved.getName() != null ? saved.getName() : saved.getUsername());

        // Use email if present, else username as JWT subject
        String subject = saved.getEmail() != null && !saved.getEmail().isBlank()
                ? saved.getEmail()
                : saved.getUsername();
        String jwt = jwtUtil.generateToken(subject);

        var resp = new AuthResponse();
        resp.setUser(uv);
        resp.setAccessToken(jwt);
        resp.setExpiresIn(jwtUtil.getJwtExpirationMs() / 1000);
        resp.setTokenType("Bearer");
        return resp;
    }
}