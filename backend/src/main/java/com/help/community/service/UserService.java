package com.help.community.service;

import com.help.community.dto.UserDTO;
import com.help.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio para operaciones con usuarios.
 * Garantiza que nunca se expongan datos sensibles.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene una página de usuarios sin exponer información sensible.
     */
    public Page<UserDTO> getSafeUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserDTO.builder()
                        .id(user.getUser_id())
                        .name(user.getName())
                        .role(user.getMainRole())
                        .build());
    }

    /**
     * Carga los detalles del usuario a partir del email.
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
