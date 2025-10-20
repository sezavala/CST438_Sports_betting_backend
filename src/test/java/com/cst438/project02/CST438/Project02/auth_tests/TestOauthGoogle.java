package com.CST438.Project02.CST438.Project02.auth_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cst438.project02.auth.dto.AuthResponse;
import com.cst438.project02.auth.dto.GoogleLoginRequest;
import com.cst438.project02.auth.dto.GoogleUserInfo;
import com.cst438.project02.auth.infra.GoogleTokenVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TestOauthGoogle {

    @MockitoBean
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGoogleLogin_ValidToken_Success() throws Exception {
        // This is a mock successful user being stored.
        GoogleUserInfo mockUserInfo = new GoogleUserInfo();
        mockUserInfo.setId("123456789");
        mockUserInfo.setEmail("test@example.com");
        mockUserInfo.setName("Test User");
        mockUserInfo.setEmailVerified(true);

        // When(...) is used to create a fake version of googleTokenVerifier: This doesn't execute code,
        // but it executes what you tell it to (mockUserInfo).
        when(googleTokenVerifier.verify(anyString())).thenReturn(mockUserInfo);

        // Create a googleLoginRequest with a "valid token"
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("mock-valid-token");

        // Create POST HTTP request using endpoint and request ID Token
        // restTemplate makes an HTTP call to the app at the url endpoint
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/google",
                request,
                AuthResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getUser());
        assertEquals("test@example.com", response.getBody().getUser().getEmail());
    }
}