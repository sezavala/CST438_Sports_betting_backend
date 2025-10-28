package com.cst438.project02.auth.service;


import com.cst438.project02.auth.config.JwtUtil;
import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleTokenResponse;
import com.cst438.project02.auth.dto.GoogleUserInfo;
import com.cst438.project02.auth.dto.LoginRequest;
import com.cst438.project02.auth.dto.RegisterRequest;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;
import com.cst438.project02.entity.User;
import com.cst438.project02.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

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
    public AuthResponse loginWithGoogleCode(String code, String codeVerifier) throws Exception {
        // Exchange authorization code for tokens
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        GoogleTokenResponse tokenResponse = mapper.readValue(response.getBody(), GoogleTokenResponse.class);

        // Get user info from Google
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(tokenResponse.getAccess_token());
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<GoogleUserInfo> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                userInfoRequest,
                GoogleUserInfo.class
        );

        GoogleUserInfo googleUser = userInfoResponse.getBody();

        // Find or create user
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