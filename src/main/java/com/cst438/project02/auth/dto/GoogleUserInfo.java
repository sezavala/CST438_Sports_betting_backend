package com.cst438.project02.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Google information format
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private boolean emailVerified;
    // Optional pictureUrl: String
}
