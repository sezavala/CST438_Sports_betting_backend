package com.CST438.Project02.CST438.Project02.auth_tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.entity.User;
import com.cst438.project02.auth.service.AuthService;
import com.cst438.project02.auth.service.OAuthStateManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TestOauthGoogle {

    @MockitoBean
    private AuthService authService;

    @Autowired
    private OAuthStateManager stateManager;

    @Autowired
    private TestRestTemplate restTemplate;

    private User mockUser;
    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        // Create mock user
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@gmail.com")
                .name("Test User")
                .googleId("google-123456789")
                .build();

        // Create mock auth response
        mockAuthResponse = new AuthResponse();
        mockAuthResponse.setAccessToken("mock-jwt-token-12345");
        mockAuthResponse.setTokenType("Bearer");
        mockAuthResponse.setExpiresIn(3600000L);

        AuthResponse.UserData userData = new AuthResponse.UserData();
        userData.setId("1");
        userData.setEmail("test@gmail.com");
        userData.setName("Test User");
        mockAuthResponse.setUser(userData);
    }

    @Test
    void testGoogleOAuthStart_RedirectsToGoogle() {
        // Test that the start endpoint responds (it will redirect to Google)
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/auth/google/start",
                String.class
        );

        // Should redirect (302) or return OK
        assertTrue(
                response.getStatusCode() == HttpStatus.FOUND ||
                        response.getStatusCode() == HttpStatus.OK,
                "Expected redirect or OK status, got: " + response.getStatusCode()
        );
    }

    @Test
    void testGoogleOAuthStatus_WaitingState() {
        // Create a waiting state
        String stateId = "test-state-waiting-123";
        stateManager.createWaitingState(stateId);

        // Poll the status endpoint
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/v1/auth/google/status/" + stateId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("waiting", response.getBody().get("status"));
    }

    @Test
    void testGoogleOAuthStatus_SuccessState() throws Exception {
        // Mock the auth service
        when(authService.loginWithGoogleCode(anyString(), any()))
                .thenReturn(mockAuthResponse);

        // Create a state and immediately set it to success
        String stateId = "test-state-success-456";
        stateManager.createWaitingState(stateId);
        stateManager.setSuccess(stateId, mockAuthResponse);

        // Poll the status endpoint
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/v1/auth/google/status/" + stateId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));

        // Verify auth data is present
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(data);
        assertEquals("mock-jwt-token-12345", data.get("accessToken"));
        assertEquals("Bearer", data.get("tokenType"));

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) data.get("user");
        assertNotNull(user);
        assertEquals("test@gmail.com", user.get("email"));
        assertEquals("Test User", user.get("name"));
    }

    @Test
    void testGoogleOAuthStatus_InvalidState() {
        // Test with invalid state ID
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/api/v1/auth/google/status/invalid-state-does-not-exist",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify error response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testGoogleOAuthCallback_ProcessesSuccessfully() throws Exception {
        // Mock the auth service to return successful auth response
        when(authService.loginWithGoogleCode(anyString(), any()))
                .thenReturn(mockAuthResponse);

        // Create a state for the callback
        String stateId = "callback-state-789";
        stateManager.createWaitingState(stateId);

        // Simulate Google's callback with authorization code
        String callbackUrl = String.format(
                "/api/v1/auth/google/callback?code=mock-google-auth-code&state=%s",
                stateId
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
                callbackUrl,
                String.class
        );

        // Verify callback processed successfully
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(
                response.getBody().contains("Authentication successful") ||
                        response.getBody().contains("successful"),
                "Expected success message in response"
        );
    }

    @Test
    void testGoogleOAuthCallback_HandlesError() throws Exception {
        // Mock the auth service to throw an exception
        when(authService.loginWithGoogleCode(anyString(), any()))
                .thenThrow(new RuntimeException("Invalid authorization code"));

        // Create a state for the callback
        String stateId = "callback-error-state-999";
        stateManager.createWaitingState(stateId);

        // Simulate Google's callback with invalid code
        String callbackUrl = String.format(
                "/api/v1/auth/google/callback?code=invalid-code&state=%s",
                stateId
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
                callbackUrl,
                String.class
        );

        // Verify error was handled
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Still returns OK with error HTML
        assertNotNull(response.getBody());
        assertTrue(
                response.getBody().contains("Authentication failed") ||
                        response.getBody().contains("failed"),
                "Expected error message in response"
        );
    }

    @Test
    void testGoogleOAuthFlow_CompleteWorkflow() throws Exception {
        // Mock the auth service
        when(authService.loginWithGoogleCode(anyString(), any()))
                .thenReturn(mockAuthResponse);

        // Step 1: Create a waiting state (simulating /start endpoint)
        String stateId = "workflow-state-complete";
        stateManager.createWaitingState(stateId);

        // Step 2: Check status - should be waiting
        ResponseEntity<Map<String, Object>> statusResponse1 = restTemplate.exchange(
                "/api/v1/auth/google/status/" + stateId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertEquals("waiting", statusResponse1.getBody().get("status"));

        // Step 3: Simulate Google callback
        String callbackUrl = String.format(
                "/api/v1/auth/google/callback?code=valid-code&state=%s",
                stateId
        );
        ResponseEntity<String> callbackResponse = restTemplate.getForEntity(
                callbackUrl,
                String.class
        );
        assertEquals(HttpStatus.OK, callbackResponse.getStatusCode());

        // Step 4: Check status again - should now be success
        ResponseEntity<Map<String, Object>> statusResponse2 = restTemplate.exchange(
                "/api/v1/auth/google/status/" + stateId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertEquals(HttpStatus.OK, statusResponse2.getStatusCode());
        assertEquals("success", statusResponse2.getBody().get("status"));

        // Verify auth data
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) statusResponse2.getBody().get("data");
        assertNotNull(data);
        assertEquals("mock-jwt-token-12345", data.get("accessToken"));
    }
}