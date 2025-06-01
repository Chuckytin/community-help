package com.help.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para la creación de una nueva solicitud de ayuda.
 * Valida los datos antes de convertirlos en una entidad Request.
 */
@Data
@Builder
public class CreateRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Category cannot be empty")
    private String category;

    private Double latitude;
    private Double longitude;
    private LocalDateTime deadline;

}
