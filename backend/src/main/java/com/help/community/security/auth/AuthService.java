package com.help.community.security.auth;

import com.help.community.dto.AuthResponse;
import com.help.community.dto.UserDTO;
import com.help.community.exception.EmailAlreadyExistsException;
import com.help.community.model.User;
import com.help.community.repository.UserRepository;
import com.help.community.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

//    public User register(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.saveAndFlush(user);
//    }

    /**
     * Registra un nuevo usuario, guarda su información en la base de datos
     * y retorna un JWT junto con los datos del usuario.
     *
     * @param request Datos de registro del usuario
     * @return JWT con datos del usuario
     */
    public JwtResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        var user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return JwtResponse.of(jwtToken, convertToDTO(savedUser));
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getUser_id())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getMainRole())
                .build();
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
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return JwtResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Renueva un token JWT siempre que sea válido y no haya expirado completamente.
     *
     * @param token Token JWT actual
     * @return Nuevo token JWT
     */
    public AuthResponse refreshToken(String token) {
        String refreshedToken = jwtService.refreshToken(token);
        return AuthResponse.builder()
                .token(refreshedToken)
                .build();
    }

}
