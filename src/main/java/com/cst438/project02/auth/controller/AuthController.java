package com.cst438.project02.auth.controller;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.LoginRequest;
import com.cst438.project02.auth.dto.RegisterRequest;
import com.cst438.project02.auth.service.AuthService;
import com.cst438.project02.auth.service.OAuthStateManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OAuthStateManager stateManager;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Route 1: Start OAuth flow - opens browser
    @GetMapping("/google/start")
    public void startGoogleAuth(HttpServletResponse response) throws IOException {
        // Generate unique state ID for this OAuth session
        String stateId = UUID.randomUUID().toString();
        stateManager.createWaitingState(stateId);

        // Build Google OAuth URL
        String authUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "client_id=%s&" +
                        "redirect_uri=%s&" +
                        "response_type=code&" +
                        "scope=openid%%20email%%20profile&" +
                        "state=%s&" +
                        "access_type=offline",
                clientId, redirectUri, stateId
        );

        // Redirect to Google
        response.sendRedirect(authUrl);
    }

    // Route 2: Poll for OAuth completion status
    @GetMapping("/google/status/{stateId}")
    public ResponseEntity<Map<String, Object>> checkGoogleAuthStatus(@PathVariable String stateId) {
        OAuthStateManager.OAuthState state = stateManager.getState(stateId);

        if (state == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid state ID"));
        }

        if ("success".equals(state.getStatus())) {
            // Clean up and return auth response
            stateManager.removeState(stateId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", state.getAuthResponse()
            ));
        } else if ("error".equals(state.getStatus())) {
            stateManager.removeState(stateId);
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", state.getError()
            ));
        }

        // Still waiting
        return ResponseEntity.ok(Map.of("status", "waiting"));
    }

    // Route 3: Google OAuth callback
    @GetMapping("/google/callback")
    public ResponseEntity<String> handleGoogleCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String stateId) {

        try {
            // Exchange code for tokens and create user
            AuthResponse authResponse = authService.loginWithGoogleCode(code, null);
            stateManager.setSuccess(stateId, authResponse);

            return ResponseEntity.ok(
                    "<html><body><h1>Authentication successful!</h1>" +
                            "<p>You can close this window and return to the app.</p></body></html>"
            );
        } catch (Exception e) {
            stateManager.setError(stateId, e.getMessage());
            return ResponseEntity.ok(
                    "<html><body><h1>Authentication failed</h1>" +
                            "<p>" + e.getMessage() + "</p></body></html>"
            );
        }
    }
}