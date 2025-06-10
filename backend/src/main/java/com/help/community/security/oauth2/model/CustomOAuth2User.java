package com.help.community.security.oauth2.model;

import com.help.community.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * Representa un usuario autenticado a través de OAuth2
 */
public class CustomOAuth2User extends User implements OAuth2User {

    private final Map<String, Object> attributes;

    /**
     * Crea una instancia de CustomOAuth2User con los datos del usuario local y atributos de OAuth2.
     */
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        super(user.getEmail(), user.getName(), user.getPassword());
        this.attributes = attributes;
    }

    /**
     * Obtiene los atributos proporcionados por el proveedor OAuth2.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Retorna el nombre del usuario, en este caso, su email.
     */
    @Override
    public String getName() {
        return getEmail();
    }
}
