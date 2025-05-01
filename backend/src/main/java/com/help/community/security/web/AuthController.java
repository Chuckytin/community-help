package com.help.community.security.web;

import com.help.community.model.User;
import com.help.community.security.jwt.JwtTokenUtil;
import com.help.community.security.services.UserDetailsImpl;
import com.help.community.security.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para manejar las operaciones de autenticación.
 * <p>
 * Expone endpoints públicos para:
 * - Registro de nuevos usuarios (/api/auth/register)
 * - Autenticación de usuarios existentes (/api/auth/login)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Maneja las peticiones de autenticación (login).
     * - Autentica al usuario.
     * - Establece la autenticación en el contexto de seguridad.
     * - Genera el token JWT.
     * - Obtiene los detalles del usuario para la respuesta.
     * - Devuelve la respuesta con el token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenUtil.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getName()
        ));
    }

    /**
     * Maneja el registro de nuevos usuarios en el sistema.
     * - Verifica si el email ya existe.
     * - Crea y guarda el nuevo usuario.
     * - Devuelve la respuesta exitosa.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        if (userDetailsService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: El email ya está en uso!");
        }

        User user = userDetailsService.saveUser(registerRequest);

        return ResponseEntity.ok("Usuario registrado exitosamente!");
    }

}
