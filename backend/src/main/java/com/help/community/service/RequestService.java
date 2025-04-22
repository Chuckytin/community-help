package com.help.community.service;

import com.help.community.dto.RequestResponseDTO;
import com.help.community.dto.UserDTO;
import com.help.community.model.Request;
import com.help.community.repository.RequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Clase Service para transformar las entidades DTOs, centraliza la lógica de mapeo.
 * Convierte una entidad Request a su DTO y mapea el creator al UserDTO.
 */
@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Obtiene solicitudes PAGINADAS.
     */
    public Page<RequestResponseDTO> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable)
                .map(this::toDTO);
    }

    /**
     * Convierte una entidad Request a su DTO correspondiente.
     */
    public RequestResponseDTO toDTO(Request request) {
        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setCategory(request.getCategory());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());

        UserDTO creatorDTO = new UserDTO();
        creatorDTO.setId(request.getCreator().getId());
        creatorDTO.setName(request.getCreator().getName());
        creatorDTO.setEmail(request.getCreator().getEmail());
        creatorDTO.setRole(request.getCreator().getRole());
        dto.setCreator(creatorDTO);

        return dto;
    }

}
