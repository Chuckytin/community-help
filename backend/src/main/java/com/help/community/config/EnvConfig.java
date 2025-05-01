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

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

}