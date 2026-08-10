package com.portfolio.financas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String tipo = "Bearer";

    public TokenResponse(String token) {
        this.token = token;
    }
}
