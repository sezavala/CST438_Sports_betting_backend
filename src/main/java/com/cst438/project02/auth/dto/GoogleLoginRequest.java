package com.cst438.project02.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// Google login request format
public class GoogleLoginRequest {

    @NotBlank
    private String idToken;
}
