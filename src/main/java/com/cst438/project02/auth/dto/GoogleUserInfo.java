package com.cst438.project02.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String sub;          // Google's unique user ID
    private String email;
    private String name;
    private String picture;
    private boolean emailVerified;
}