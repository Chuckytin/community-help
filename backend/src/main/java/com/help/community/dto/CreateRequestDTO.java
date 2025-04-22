package com.help.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * DTO para la creación de una nueva solicitud de ayuda.
 * Valida los datos antes de convertirlos en una entidad Request.
 */
@Data
public class CreateRequestDTO {

    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 100, message = "El título no puede exceder los 100 carácteres.")
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String description;

    @NotBlank(message = "La categoría no puede estar vacía.")
    private String category;

}
