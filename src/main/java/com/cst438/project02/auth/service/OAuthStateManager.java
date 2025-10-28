package com.cst438.project02.auth.service;

import com.cst438.project02.auth.dto.AuthResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuthStateManager {
    private final Map<String, OAuthState> states = new ConcurrentHashMap<>();

    @Data
    public static class OAuthState {
        private String status; // "waiting", "success", "error"
        private AuthResponse authResponse;
        private String error;
    }

    public void createWaitingState(String stateId) {
        OAuthState state = new OAuthState();
        state.setStatus("waiting");
        states.put(stateId, state);
    }

    public OAuthState getState(String stateId) {
        return states.get(stateId);
    }

    public void setSuccess(String stateId, AuthResponse authResponse) {
        OAuthState state = states.get(stateId);
        if (state != null) {
            state.setStatus("success");
            state.setAuthResponse(authResponse);
        }
    }

    public void setError(String stateId, String error) {
        OAuthState state = states.get(stateId);
        if (state != null) {
            state.setStatus("error");
            state.setError(error);
        }
    }

    public void removeState(String stateId) {
        states.remove(stateId);
    }
}