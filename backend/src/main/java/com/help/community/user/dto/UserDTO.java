package com.help.community.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * DTO para representar un usuario en respuestas API.
 * Oculta campos sensibles como 'password'.
 */
@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String provider;
    private Double latitude;
    private Double longitude;
    private Map<String, Object> attributes;

    @JsonIgnore
    private String role;

    @JsonIgnore
    private String password;
}
