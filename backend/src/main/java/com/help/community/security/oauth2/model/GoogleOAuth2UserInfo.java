package com.help.community.security.oauth2.model;

import com.help.community.security.oauth2.OAuth2UserInfo;

import java.util.Map;

/**
 * Implementación concreta de OAuth2UserInfo para usuarios autenticados con Google.
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    /**
     * Crea una nueva instancia con los atributos devueltos por Google.
     */
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

}
