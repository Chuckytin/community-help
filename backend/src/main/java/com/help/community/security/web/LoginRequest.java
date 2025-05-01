package com.help.community.security.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para recibir las credenciales de login.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

}
