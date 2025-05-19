package com.help.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la actualización de una solicitud de ayuda.
 */
@Data
public class UpdateRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String status;
}
