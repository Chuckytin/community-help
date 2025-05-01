package com.help.community.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createdAt;

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

}
