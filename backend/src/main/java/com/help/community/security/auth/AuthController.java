package com.help.community.security.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para operaciones de autenticación.
 * Expone endpoints públicos para registro y login de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autentication", description = "User registration and login API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest DTO con datos de registro (email, nombre, password)
     * @return usuario creado
     */
    @PostMapping("/register")
    @Operation(summary = "User registration")
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
    @Operation(summary = "User login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authService.login(loginRequest));

    }

}
