package com.help.community.security.auth;

import com.help.community.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

/**
 * Respuesta de autenticación que contiene:
 * - El token JWT generado
 * - El tipo de token (por defecto "Bearer")
 * - Los datos del usuario autenticado
 */
@Data
@Builder
public class JwtResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private UserDTO user;

    /**
     * Crea una instancia de JwtResponse a partir del token y datos del usuario.
     *
     * @param token Token JWT
     * @param user Datos del usuario autenticado
     * @return JwtResponse con token y datos del usuario
     */
    public static JwtResponse of(String token, UserDTO user) {
        return JwtResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}