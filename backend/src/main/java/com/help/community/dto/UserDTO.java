package com.help.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para representar un usuario en respuestas API.
 * Oculta campos sensibles como 'password'.
 */
@Data
@Builder
public class UserDTO {

    private Long id;
    private String name;
    private String role;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;


}
