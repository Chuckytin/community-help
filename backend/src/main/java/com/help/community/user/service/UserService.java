package com.help.community.user.service;

import com.help.community.user.dto.UserDTO;
import com.help.community.user.dto.UserProfileDTO;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Servicio para operaciones con usuarios.
 * Garantiza que nunca se expongan datos sensibles.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    /**
     * Convierte un usuario en un DTO de perfil, incluyendo conteo de solicitudes creadas.
     */
    public UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getUser_id())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No proporcionado")
                .roles(user.getRoles().stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet()))
                .createdRequestsCount(user.getCreatedRequests().size())
                .build();
    }

    /**
     * Convierte un usuario en un DTO simple, sin información sensible.
     */
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getUser_id())
                .name(user.getName())
                .role(user.getMainRole())
                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No proporcionado")
                .build();
    }

    /**
     * Obtiene una página de usuarios sin exponer información sensible.
     */
    public Page<UserDTO> getSafeUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserDTO.builder()
                        .id(user.getUser_id())
                        .name(user.getName())
                        .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "No proporcionado")
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
