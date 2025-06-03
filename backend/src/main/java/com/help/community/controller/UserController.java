package com.help.community.controller;

import com.help.community.dto.UserDTO;
import com.help.community.dto.UserProfileDTO;
import com.help.community.exception.ResourceNotFoundException;
import com.help.community.model.User;
import com.help.community.repository.UserRepository;
import com.help.community.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controlador para operaciones con usuarios.
 * Expone solo endpoints seguros con datos filtrados.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Obtiene una lista paginada de usuarios con información pública.
     *
     * @param pageable Parámetros de paginación y orden.
     * @return Página de UserDTOs.
     */
    @GetMapping
    public Page<UserDTO> getAllUsers(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userService.getSafeUsers(pageable);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param user Entidad User con los datos del nuevo usuario.
     * @return Usuario creado.
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @param userDetails Usuario autenticado.
     * @return DTO con información del perfil del usuario.
     * @throws UsernameNotFoundException si el usuario no se encuentra.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(userService.toProfileDTO(user));
    }

    /**
     * Obtiene información básica de un usuario por su ID.
     * Solo accesible para administradores.
     *
     * @param id ID del usuario que se desea consultar.
     * @return DTO con información pública del usuario (nombre, rol).
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long id) {
        //TODO: Que solo puedan acceder los administradores con token de admin
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(userService.toDTO(user));
    }

    /**
     *
     *
     * @param id
     * @param roles
     * @return
     */
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateRoles(@PathVariable("userId") Long id, @RequestBody Set<User.Role> roles) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

}
