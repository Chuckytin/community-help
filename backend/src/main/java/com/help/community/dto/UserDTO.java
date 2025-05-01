package com.help.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * DTO para representar un usuario en respuestas API.
 * Oculta campos sensibles como 'password'.
 */
@Data
public class UserDTO {

    private Long id;
    private String name;
    private String role;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;


}
