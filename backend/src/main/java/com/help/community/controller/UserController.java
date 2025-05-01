package com.help.community.controller;

import com.help.community.dto.UserDTO;
import com.help.community.model.User;
import com.help.community.repository.UserRepository;
import com.help.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones con usuarios.
 * Expone solo endpoints seguros con datos filtrados.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDTO> getAllUsers(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return userService.getSafeUsers(pageable);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
