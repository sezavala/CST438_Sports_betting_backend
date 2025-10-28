package com.cst438.project02.auth.infra;

import com.cst438.project02.auth.exception.InvalidGoogleIdException;
import com.cst438.project02.auth.dto.GoogleUserInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifier {
    @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId;

    // Method used to check and return a users information based on Google
    public GoogleUserInfo verify(String idToken) throws GeneralSecurityException, IOException {
        try {
            GoogleIdTokenVerifier verifiedBuilder = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singleton(clientId))
                    .setIssuer("https://accounts.google.com")
                    .build();

            GoogleIdToken googleIdToken = verifiedBuilder.verify(idToken);
            if (googleIdToken == null) {
                throw new RuntimeException("Invalid ID token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.setSub(payload.getSubject());
            userInfo.setName((String) payload.get("name"));
            userInfo.setEmail(payload.getEmail());
            userInfo.setEmailVerified(payload.getEmailVerified());

            return userInfo;
        } catch (Exception e) {
            throw new InvalidGoogleIdException("Invalid Google ID token", e);
        }
    }
}
