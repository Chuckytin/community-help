package com.help.community.security.services;

import com.help.community.model.User;
import com.help.community.repository.UserRepository;
import com.help.community.security.web.RegisterRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga los detalles del usuario desde la base de datos.
 * <p>
 * Implementa la interfaz UserDetailsService que requiere:
 * - Un método (email) para cargar usuarios por su identificador.
 * - Convierte la entidad User en un UserDetails.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Carga un usuario por email que actúa como username.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        return UserDetailsImpl.build(user);
    }

    /**
     * Verifica si un email ya está registrado.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Guarda un nuevo usuario encriptando su contraseña.
     */
    @Transactional
    public User saveUser(RegisterRequest registerRequest) {
        User user = new User(
                registerRequest.getEmail(),
                registerRequest.getName(),
                passwordEncoder.encode(registerRequest.getPassword()));

        return userRepository.save(user);
    }

}
