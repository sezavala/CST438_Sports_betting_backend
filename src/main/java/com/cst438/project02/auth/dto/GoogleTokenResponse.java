package com.cst438.project02.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // Add this annotation
public class GoogleTokenResponse {
    private String access_token;
    @Getter
    private String id_token;
    private String refresh_token;
    private String token_type;
    private long expires_in;
    // scope field is ignored automatically now
}