package com.help.community.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase que busca en el directorio raíz ./.env para centralizar el acceso a las variables
 * Proporciona valores por defecto.
 */
@Configuration
public class EnvConfig {

    private static final int DEFAULT_JWT_EXPIRATION_MS = 86400000; // 24 horas
    private static final int SHORT_JWT_EXPIRATION_MS = 3600000;    // 1 hora
    private static final String DEFAULT_JWT_SECRET = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6";

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    /**
     * Configuración del secreto JWT.
     */
    @Bean
    public String jwtSecret(Dotenv dotenv) {
        return dotenv.get("JWT_SECRET", DEFAULT_JWT_SECRET);
    }

    /**
     * Tiempo de expiración del token JWT en ms.
     * - Producción: 24 horas.
     * - Desarrollo: 1 hora.
     */
    @Bean
    public Integer jwtExpiration(Dotenv dotenv) {
        return Integer.parseInt(dotenv.get("JWT_EXPIRATION",
                Boolean.parseBoolean(System.getProperty("dev.mode")) ?
                        String.valueOf(SHORT_JWT_EXPIRATION_MS) :
                        String.valueOf(DEFAULT_JWT_EXPIRATION_MS)));
    }
}
