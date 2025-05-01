package com.help.community.controller;

import com.help.community.dto.CreateRequestDTO;
import com.help.community.dto.RequestResponseDTO;
import com.help.community.model.Request;
import com.help.community.model.User;
import com.help.community.repository.RequestRepository;
import com.help.community.repository.UserRepository;
import com.help.community.security.services.UserDetailsImpl;
import com.help.community.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para manejar las operaciones relacionadas con solicitudes de ayuda.
 * Usa DTOs para las respuestas y validación de entrada.
 */
@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestService requestService;

    public RequestController(
            RequestRepository requestRepository,
            UserRepository userRepository,
            RequestService requestService
    ) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestService = requestService;
    }

    /**
     * Obtiene todas las solicitudes en formato DTO.
     * Convierte cada Request a RequestResponseDTO.
     * Solo usuarios registrados.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<RequestResponseDTO> getAllRequests(
            @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return requestService.getAllRequests(pageable);
    }

    /**
     * Crea una nueva solicitud y devuelve el DTO de la solicitud creada.
     * Solo para usuarios autenticados.
     * - Obtiene el usuario autenticado del contexto de seguridad.
     * - Busca el usuario completo en la base de datos.
     *
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RequestResponseDTO> createRequest(@Valid @RequestBody CreateRequestDTO requestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User creator = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Request request = new Request();
        request.setTitle(requestDTO.getTitle());
        request.setDescription(requestDTO.getDescription());
        request.setCategory(requestDTO.getCategory());
        request.setCreator(creator);

        Request savedRequest = requestRepository.save(request);

        RequestResponseDTO responseDTO = requestService.toDTO(savedRequest);

        return ResponseEntity
                .created(URI.create("/api/requests/" + savedRequest.getId()))
                .body(responseDTO);
    }

}
