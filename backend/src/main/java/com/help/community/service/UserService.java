package com.help.community.service;

import com.help.community.dto.UserDTO;
import com.help.community.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio para operaciones con usuarios.
 * Garantiza que nunca se expongan datos sensibles.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Page<UserDTO> getSafeUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getUser_id());
                    dto.setName(user.getName());
                    dto.setRole(user.getMainRole());
                    return dto;
                });
    }

}
