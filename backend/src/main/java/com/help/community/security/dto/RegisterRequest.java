package com.help.community.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitudes de registro.
 * Contiene los datos requeridos para crear una nueva cuenta de usuario.
 */
@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank @Size(min = 3, max = 50)
    private String name;

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}