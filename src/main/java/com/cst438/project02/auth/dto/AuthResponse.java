package com.cst438.project02.auth.dto;

import com.cst438.project02.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Authentication response format
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserView user;

    public static AuthResponse fromUser(User user, String token, long expiresIn) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setExpiresIn(expiresIn);
        
        UserView userView = new UserView();
        userView.setId(String.valueOf(user.getId()));
        userView.setEmail(user.getEmail());
        userView.setName(user.getName());
        response.setUser(userView);
        
        return response;
    }
}