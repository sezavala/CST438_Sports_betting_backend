package com.cst438.project02.auth.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class GoogleTokenResponse {
    private String access_token;
    @Getter
    private String id_token;
    private String refresh_token;
    private String token_type;
    private long expires_in;
}