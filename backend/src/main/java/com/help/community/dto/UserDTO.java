package com.help.community.dto;

import lombok.Data;

/**
 * DTO para representar un usuario en respuestas API.
 * Oculta campos sensibles como 'password'.
 */
@Data
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String role;

}
