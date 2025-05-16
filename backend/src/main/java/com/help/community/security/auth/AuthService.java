package com.help.community.security.auth;

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
     * Registra un nuevo usuario y devuelve su token JWT
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
     * Autentica un usuario existente y devuelve su token JWT
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
}
