package com.help.community.security.web;

/**
 * DTO para la respuesta de autenticación exitosa.
 * Contiene el token JWT para usar en las subsiguientes peticiones.
 */
public class JwtResponse {

    private String token;
    private String type;
    private Long id;
    private String email;
    private String name;

    public JwtResponse(String token, Long id, String email, String name) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
