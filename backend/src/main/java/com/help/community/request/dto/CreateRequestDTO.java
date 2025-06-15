package com.help.community.request.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @Future(message = "La fecha debe ser futura")
    private LocalDateTime deadline;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitude;

}
