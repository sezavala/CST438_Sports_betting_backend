package com.cst438.project02.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// User information format
public class UserView {
    private String id;
    private String email;
    private String name;
    // Optional pictureUrl: String
}
