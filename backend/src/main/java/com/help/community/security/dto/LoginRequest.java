package com.help.community.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitudes de login.
 * Contiene las credenciales necesarias para autenticar a un usuario.
 */
@Data
public class LoginRequest {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}