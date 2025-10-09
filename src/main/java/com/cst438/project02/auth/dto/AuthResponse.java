package com.cst438.project02.auth.dto;

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
    private UserView user;
}
