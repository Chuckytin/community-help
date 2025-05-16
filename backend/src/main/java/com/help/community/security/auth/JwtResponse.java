package com.help.community.security.auth;

import com.help.community.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

/**
 * Respuesta de autenticación que contiene:
 * - Token JWT para autorización
 * - Tipo de token (siempre Bearer)
 * - Datos básicos del usuario autenticado
 */
@Data
@Builder
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private UserDTO user;

    /**
     *
     * @param token
     * @param user
     * @return
     */
    public static JwtResponse of(String token, UserDTO user) {
        return JwtResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}