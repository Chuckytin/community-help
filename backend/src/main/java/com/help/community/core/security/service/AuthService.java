package com.help.community.core.security.service;

import com.help.community.user.dto.UserDTO;
import com.help.community.core.exception.EmailAlreadyExistsException;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import com.help.community.core.security.dto.JwtResponse;
import com.help.community.core.security.dto.LoginRequest;
import com.help.community.core.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la autenticación y registro de usuarios,
 * así como la emisión y renovación de tokens JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario, guarda su información en la base de datos
     * y retorna un JWT junto con los datos del usuario.
     *
     * @param request Datos de registro del usuario
     * @return JWT con datos del usuario
     */
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        var user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .provider("local")
                .build();

        User savedUser = userRepository.save(user);
        return buildJwtResponse(savedUser);
    }

    /**
     * Autentica al usuario y genera un token JWT si las credenciales son válidas.
     *
     * @param request Credenciales de acceso
     * @return Token JWT
     */
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return buildJwtResponse(user);
    }

    /**
     * Renueva un token JWT siempre que sea válido y no haya expirado completamente.
     *
     * @param token Token JWT actual
     * @return Nuevo token JWT
     */
    public JwtResponse refreshToken(String token) {
        String refreshedToken = jwtService.refreshToken(token);
        return JwtResponse.builder()
                .accessToken(refreshedToken)
                .build();
    }

    private JwtResponse buildJwtResponse(User user) {
        String token = jwtService.generateToken(user);
        return JwtResponse.builder()
                .accessToken(token)
                .user(convertToDTO(user))
                .expiresAt(jwtService.extractExpiration(token).toInstant())
                .build();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getMainRole())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

}
