package com.help.community.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * DTO para exponer el perfil del usuario autenticado.
 * Incluye información básica y conteo de solicitudes creadas.
 */
@Data
@Builder
public class UserProfileDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private Set<String> roles;
    private int createdRequestsCount;
    private Double latitude;
    private Double longitude;
}
