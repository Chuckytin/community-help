package com.help.community.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 */
@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Size(min = 8)
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Número de teléfono inválido")
    private String phoneNumber;
}
