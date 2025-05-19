package com.help.community.security.auth;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operaciones de autenticación de usuarios.
 * Proporciona endpoints públicos para registro, login y renovación de tokens.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autentication", description = "User authentication API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     *
     * @param registerRequest DTO con datos de registro (email, nombre, password)
     * @return Token JWT junto con los datos del usuario
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     *
     * @param loginRequest DTO con credenciales (email, password)
     * @return token JWT
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Renueva un token JWT si es válido y no ha expirado completamente.
     *
     * @param authHeader Encabezado Authorization con el token actual
     * @return Nuevo token JWT o error si no es válido
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String token = authHeader.substring(7);
            return ResponseEntity.ok(authService.refreshToken(token));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
