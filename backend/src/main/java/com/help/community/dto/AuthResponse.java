package com.help.community.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * DTO para la respuesta de autenticación JWT.
 * Contiene el token, tipo y fecha de expiración.
 */
@Data
@Builder
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Instant expiresAt;
}