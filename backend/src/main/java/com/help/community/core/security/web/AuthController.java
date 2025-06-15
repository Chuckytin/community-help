package com.help.community.core.security.web;

import com.help.community.core.security.service.AuthService;
import com.help.community.core.security.dto.JwtResponse;
import com.help.community.core.security.dto.LoginRequest;
import com.help.community.core.security.dto.RegisterRequest;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones de autenticación de usuarios.
 * Proporciona endpoints públicos para registro, login y renovación de tokens.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autentication", description = "User authentication API")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     *
     * @param request DTO con datos de registro (email, nombre, password)
     * @return Token JWT junto con los datos del usuario
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     *
     * @param request DTO con credenciales (email, password)
     * @return token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            System.out.println("Intento de login para: " + request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                System.out.println("Contraseña incorrecta para: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            JwtResponse response = authService.login(request);
            System.out.println("Login exitoso para: " + request.getEmail());

            // Devuelve también el token como cookie
            ResponseCookie cookie = ResponseCookie.from("token", response.getAccessToken())
                    .httpOnly(true)
                    .secure(false) // Cambiar a true en producción con HTTPS
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 días
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error de autenticación: " + e.getMessage()));
        }
    }

    /**
     * Renueva un token JWT si es válido y no ha expirado completamente.
     *
     * @param authHeader Encabezado Authorization con el token actual
     * @return Nuevo token JWT o error si no es válido
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Invalid authorization header");
        }
        return ResponseEntity.ok(authService.refreshToken(authHeader.substring(7)));
    }

    @GetMapping(value = "/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "OK");
        response.put("version", "1.0.0");
        return response;
    }

}
