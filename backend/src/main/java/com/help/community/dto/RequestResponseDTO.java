package com.help.community.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una solicitud de ayuda.
 * Incluye información básica y usuarios involucrados (creador y voluntario).
 */
@Data
@Builder
public class RequestResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    /**
     * Usuario creador de la solicitud.
     * Se expone el nombre y el rol.
     */
    private UserDTO creator;

    /**
     * Voluntario asignado.
     * Se expone el nombre y el rol.
     */
    private UserDTO volunteer;

    private Double latitude;
    private Double longitude;

}
