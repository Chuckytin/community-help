package com.help.community.core.security.web;

import com.help.community.core.security.service.AuthService;
import com.help.community.core.security.dto.JwtResponse;
import com.help.community.core.security.dto.LoginRequest;
import com.help.community.core.security.dto.RegisterRequest;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Authenticate user")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
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

}
