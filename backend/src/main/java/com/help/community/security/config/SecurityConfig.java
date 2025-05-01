package com.help.community.security.config;

import com.help.community.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad.
 * <p>
 * En la clase se definen:
 * - Se definen los endpoints públicos y cuáles requieren autenticación.
 * - La política del manejo de las sesiones.
 * - El tipo de encriptación utilizado para las contraseñas.
 * - La cadena de filtros de seguridad.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    /**
     * Configura las reglas de seguridad HTTP (HttpSecurity).
     * <p>
     * - Deshabilita CSRF (no es necesario para las APIs)
     * - /api/auth/ Permite el acceso público a endpoints de autenticación
     * - /api-docs/ Permite el acceso a la documentación de Swagger
     * - Requiere autenticación para cualquier otro endpoint
     * - Establece la política de sesión como stateless (sin estado), no se mantendrán sesiones HTTP entre peticiones
     * - Añade el filtro JWT personalizado antes del filtro de autenticación estándar por usuario/contraseña
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de encriptación para las contraseñas.
     * <p>
     * Usa BCrypt (algoritmo fuerte de hashing)
     * - Genera un salt aleatorio para cada contraseña
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor del AuthenticationManager.
     * Es necesario para el proceso de autenticación en el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
