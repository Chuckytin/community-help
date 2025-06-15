package com.help.community.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

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

    private String status; // Made optional

    private LocalDateTime deadline;
}
