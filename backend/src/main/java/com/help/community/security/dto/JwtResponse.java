package com.help.community.security.dto;

import com.help.community.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Respuesta de autenticación que contiene:
 * - El token JWT generado
 * - El tipo de token (por defecto "Bearer")
 * - Los datos del usuario autenticado
 */
@Data
@Builder
public class JwtResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserDTO user;
    private Instant expiresAt;
}