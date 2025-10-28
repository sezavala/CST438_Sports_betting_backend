package com.cst438.project02.auth.dto;

import com.cst438.project02.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserData user;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserData {
        private String id;
        private String email;
        private String name;
    }

    public static AuthResponse fromUser(User user, String token, long expiresIn) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setExpiresIn(expiresIn);

        UserData userData = new UserData();
        userData.setId(String.valueOf(user.getId()));
        userData.setEmail(user.getEmail());
        userData.setName(user.getName());
        response.setUser(userData);

        return response;
    }
}