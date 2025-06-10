package com.help.community.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada para propiedades JWT.
 * Los valores se cargan desde application.properties con prefijo "jwt"
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey;
    private long expiration;
    private long refreshGracePeriod;
}
