package com.help.community.request.controller;

import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.request.dto.*;
import com.help.community.request.event.RequestStatusChangedEvent;
import com.help.community.core.exception.ResourceNotFoundException;
import com.help.community.request.model.Request;
import com.help.community.request.service.GeocodingService;
import com.help.community.user.model.User;
import com.help.community.request.repository.RequestRepository;
import com.help.community.user.repository.UserRepository;
import com.help.community.integration.OpenRouteService;
import com.help.community.request.service.RequestService;
import com.help.community.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para manejar las operaciones relacionadas con solicitudes de ayuda.
 * Usa DTOs para las respuestas y validación de entrada.
 */
@Controller
@RequestMapping("/api/requests")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestService requestService;
    private final UserService userService;
    private final RequestControllerUtils requestControllerUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final GeocodingService geocodingService;
    private OpenRouteService openRouteService;

    public RequestController(RequestRepository requestRepository, UserRepository userRepository,
                             RequestService requestService, UserService userService, RequestControllerUtils requestControllerUtils,
                             ApplicationEventPublisher eventPublisher, GeocodingService geocodingService, OpenRouteService openRouteService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestService = requestService;
        this.userService = userService;
        this.requestControllerUtils = requestControllerUtils;
        this.eventPublisher = eventPublisher;
        this.geocodingService = geocodingService;
        this.openRouteService = openRouteService;
    }

    /**
     * Crea una nueva solicitud y devuelve el DTO de la solicitud creada.
     *
     * @param requestDTO Datos validados de la solicitud.
     * @return ResponseEntity con el DTO de la solicitud y HTTP 201.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRequest(
            @Valid @RequestBody CreateRequestDTO requestDTO) {

        logger.info("Recibida solicitud para crear nueva petición: {}", requestDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        User creator = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Request request = getRequest(requestDTO, creator);

        Request savedRequest = requestRepository.save(request);

        logger.info("Solicitud creada con ID: {}", savedRequest.getRequest_id());

        // Crear respuesta explícita con el ID
        Map<String, Object> response = new HashMap<>();
        response.put("request_id", savedRequest.getRequest_id());
        response.put("message", "Solicitud creada exitosamente");

        return ResponseEntity
                .created(URI.create("/api/requests/" + savedRequest.getRequest_id()))
                .body(response);
    }

    private static Request getRequest(CreateRequestDTO requestDTO, User creator) {
        Request request = new Request();
        request.setTitle(requestDTO.getTitle());
        request.setDescription(requestDTO.getDescription());
        request.setCategory(requestDTO.getCategory());
        request.setDeadline(requestDTO.getDeadline());
        request.setCreator(creator);
        request.setStatus("PENDIENTE");

        if (requestDTO.getLatitude() != null && requestDTO.getLongitude() != null) {
            request.setLocationFromLatLon(requestDTO.getLatitude(), requestDTO.getLongitude());
        }
        return request;
    }


    /**
     * Obtiene todas las solicitudes en formato DTO.
     * Convierte cada Request a RequestResponseDTO
     */
    @GetMapping
    public Page<RequestResponseDTO> getAllRequests(
            @PageableDefault(size = 25, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return requestService.getAllRequests(pageable);
    }

    /**
     * Obtiene todas las solicitudes creadas por un usuario específico.
     */
    @GetMapping("/user/{userId}")
    public List<RequestResponseDTO> getRequestsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getCreatedRequests().stream()
                .map(requestService::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las solicitudes creadas por el usuario autenticado.
     *
     */
    @GetMapping("/mine")
    public ResponseEntity<List<RequestResponseDTO>> getMyRequests(
            @AuthenticationPrincipal Object principal) {

        String username;

        if (principal instanceof CustomOAuth2User oauthUser) {
            username = oauthUser.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<RequestResponseDTO> requests = user.getCreatedRequests().stream()
                .map(requestService::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(requests);
    }

    /**
     * Obtiene una solicitud específica por su ID.
     *
     * @param id Identificador de la solicitud.
     * @return ResponseEntity con el DTO de la solicitud encontrada.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<RequestResponseDTO> getRequestById(@PathVariable("requestId") Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        return ResponseEntity.ok(requestService.toDTO(request));
    }

    /**
     * Obtiene una lista de solicitudes cercanas dentro de un radio dado,
     * filtradas por urgencia (tiempo estimado de viaje <= tiempo restante hasta el deadline)
     * y ordenadas por proximidad (menor duración estimada de viaje).
     * Usa la API de OpenRouteService para estimar la duración del trayecto en coche entre
     * el voluntario y cada solicitud.
     *
     * @param latitude Latitud del voluntario (en grados decimales).
     * @param longitude Longitud del voluntario (en grados decimales).
     * @param radiusMeters Radio de búsqueda en metros (por defecto 5000m).
     * @return Lista de solicitudes ordenadas por proximidad, filtradas por urgencia si corresponde.
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<RequestResponseDTO>> getNearbyRequests(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10000") double radiusMeters,
            @RequestParam(required = false) String transportMode) {

        if (!requestControllerUtils.isValidCoordinate(latitude, longitude)) {
            return ResponseEntity.badRequest().build();
        }

        OpenRouteService.TransportMode mode = requestControllerUtils.parseTransportMode(transportMode);

        List<Request> requests = (mode == OpenRouteService.TransportMode.DRIVING_CAR) ?
                requestRepository.findAllPendingRequests() :
                requestRepository.findNearbyPendingRequests(latitude, longitude, radiusMeters);

        List<RequestResponseDTO> result = requests.stream()
                .map(request -> {
                    RequestResponseDTO dto = requestService.toDTO(request);
                    requestControllerUtils.calculateReachability(latitude, longitude, request, dto, mode);
                    return dto;
                })
                .sorted(Comparator.comparing(dto ->
                        dto.getTravelDistance() != null ? dto.getTravelDistance() : Double.MAX_VALUE))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Asigna al usuario autenticado como voluntario de una solicitud específica.
     * Cambia automáticamente el estado de la solicitud a 'ACEPTADO'.
     */
    @PatchMapping("/{requestId}/assign-volunteer")
    public String assignVolunteer(
            @PathVariable("requestId") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        logger.info("Assigning volunteer for request ID: {}", id);
        logger.info("User: {}", userDetails.getUsername());

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        User volunteer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getCreator().equals(volunteer)) {
            throw new AccessDeniedException("No puedes aceptar tu propia solicitud");
        }

        if (!"PENDIENTE".equals(request.getStatus())) {
            throw new IllegalStateException("Solo se pueden aceptar solicitudes pendientes");
        }

        request.setVolunteer(volunteer);
        request.setStatus("ACEPTADO");
        Request savedRequest = requestRepository.save(request); // Aquí guardamos y obtenemos la solicitud actualizada

        redirectAttributes.addFlashAttribute("successMessage", "¡Has aceptado la solicitud correctamente!");
        redirectAttributes.addFlashAttribute("request", requestService.toDTO(savedRequest));

        return "redirect:/api/requests/accepted/" + id;
    }

    // Para solicitudes normales (edición)
    @GetMapping("/request/{id}")
    public String showRequest(@PathVariable Long id, Model model) {
        Optional<Request> request = requestRepository.findById(id);
        model.addAttribute("request", request);
        return "request-details";
    }

    @GetMapping("/request/accepted/{id}")
    public String showAcceptConfirmation(@PathVariable Long id, Model model) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", requestService.toDTO(request));
        }

        return "request-accepted";
    }

    /**
     * Obtiene todas las solicitudes en las que el usuario autenticado se ha ofrecido como voluntario.
     *
     * @param userDetails Usuario autenticado extraído del token JWT.
     * @return Lista de solicitudes en las que el usuario actúa como voluntario.
     */
    @GetMapping("/volunteering")
    public ResponseEntity<List<RequestResponseDTO>> getVolunteeringRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailWithVolunteeredRequests(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(user.getVolunteeredRequests().stream()
                .map(requestService::toDTO)
                .collect(Collectors.toList()));
    }

    /**
     * Actualiza el estado de una solicitud.
     * Solo pueden hacerlo el creador, el voluntario asignado o un administrador.
     */
    @PatchMapping("/{requestId}/status")
    public ResponseEntity<RequestResponseDTO> updateStatus(
            @PathVariable("requestId") Long id,
            @Valid @RequestBody UpdateStatusDTO statusDTO,
            @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        String oldStatus = request.getStatus();
        request.setStatus(statusDTO.getStatus());
        Request updatedRequest = requestRepository.save(request);

        eventPublisher.publishEvent(new RequestStatusChangedEvent(this, updatedRequest, oldStatus));

        return ResponseEntity.ok(requestService.toDTO(updatedRequest));
    }

    @PostMapping("/{requestId}/complete")
    public String completeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        // Validar que el usuario es el voluntario asignado
        if (!request.getVolunteer().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("No tienes permiso para completar esta solicitud");
        }

        request.setStatus("COMPLETADA");
        request.setCompletedAt(LocalDateTime.now());
        requestRepository.save(request);

        redirectAttributes.addFlashAttribute("successMessage",
                "¡La solicitud ha sido marcada como completada con éxito!");

        return "redirect:/request/accepted/" + requestId;
    }

    @GetMapping("/accepted/{id}")
    public String showAcceptedRequest(@PathVariable Long id, Model model) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        model.addAttribute("request", requestService.toDTO(request));
        return "request-accepted";
    }

    @PutMapping("/{requestId}/cancel-volunteer")
    public String cancelVolunteer(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) throws AccessDeniedException {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        // Validar que el usuario es el voluntario asignado
        if (!request.getVolunteer().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("No tienes permiso para cancelar esta participación");
        }

        request.setVolunteer(null);
        request.setStatus("PENDIENTE");
        requestRepository.save(request);

        redirectAttributes.addFlashAttribute("successMessage",
                "Has cancelado tu participación en esta solicitud");

        return "redirect:/home";
    }

    /**
     * Actualiza una solicitud existente si el usuario es el creador.
     * - Verificar que el usuario es el creador
     *
     * @param id ID de la solicitud a actualizar.
     * @param updateDTO Datos validados de la solicitud a actualizar.
     * @param userDetails Usuario autenticado que realiza la acción.
     * @return DTO de la solicitud actualizada.
     * @throws AccessDeniedException si el usuario no es el creador de la solicitud.
     */
    @PutMapping("/{requestId}")
    public String updateRequest(
            @PathVariable("requestId") Long id,
            @ModelAttribute @Valid UpdateRequestDTO updateDTO,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        if (result.hasErrors()) {
            model.addAttribute("request", updateDTO);
            model.addAttribute("categories", Arrays.asList("URGENTE", "NO URGENTE", "AYUDA DOMÉSTICA", "COMPAÑÍA", "OTROS"));
            return "edit-request";
        }
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (!request.getCreator().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Solo puedes actualizar tus propias solicitudes");
        }
        request.setTitle(updateDTO.getTitle());
        request.setDescription(updateDTO.getDescription());
        request.setCategory(updateDTO.getCategory());
        request.setStatus(updateDTO.getStatus() != null ? updateDTO.getStatus() : request.getStatus());
        request.setDeadline(updateDTO.getDeadline());
        requestRepository.save(request);
        return "redirect:/home";
    }

    @GetMapping("/request/edit/{id}")
    public String showEditRequestForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (!request.getCreator().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Solo puedes editar tus propias solicitudes");
        }
        RequestResponseDTO requestDTO = requestService.toDTO(request, request.getCreator());
        model.addAttribute("request", requestDTO);
        model.addAttribute("user", userService.toDTO(userRepository.findByEmail(userDetails.getUsername()).orElse(null)));
        // Lista de categorías para errores de validación (opcional)
        model.addAttribute("categories", Arrays.asList("URGENTE", "NO URGENTE", "AYUDA DOMÉSTICA", "COMPAÑÍA", "OTROS"));
        return "edit-request";
    }

    /**
     * Elimina una solicitud si el usuario es el creador o un administrador.
     * - Verifica que el usuario es el creador o admin.
     *
     * @param id ID de la solicitud a eliminar.
     * @param userDetails Usuario autenticado que realiza la acción.
     * @return ResponseEntity con headers informativos y estado HTTP 200.
     * @throws AccessDeniedException si el usuario no tiene permisos para eliminarla.
     */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable("requestId") Long id,
            @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!request.getCreator().equals(user) && !user.getRoles().contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("You don't have permission to delete this request");
        }

        requestRepository.delete(request);

        return ResponseEntity.ok()
                .header("message", "Request deleted successfully")
                .header("requestId", id.toString())
                .header("deletedAt", LocalDateTime.now().toString())
                .build();
    }

    @PostMapping("/search-by-location")
    public ResponseEntity<List<RequestNearbyDTO>> searchRequestsByLocation(@RequestBody LocationSearchDTO searchDTO) {
        try {
            if (!geocodingService.existsMunicipality(searchDTO.getCity())) {
                return ResponseEntity.badRequest().body(List.of());
            }
            double[] coords = geocodingService.getCoordinates(searchDTO.getCity());
            List<Request> requests = requestRepository.findNearbyPendingRequests(coords[0], coords[1], 10000);
            List<RequestNearbyDTO> dtos = requests.stream()
                    .map(request -> requestService.toNearbyDTO(request, coords[0], coords[1]))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        } catch (Exception e) {
            logger.error("Error al buscar solicitudes por ubicación: {}", searchDTO.getCity(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/city-coordinates")
    public ResponseEntity<Map<String, Double>> getCityCoordinates(@RequestParam String city) {
        try {
            double[] coords = geocodingService.getCoordinates(city);
            return ResponseEntity.ok(Map.of(
                    "latitude", coords[0],
                    "longitude", coords[1]
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Data
    static class LocationSearchDTO {
        private String city;
    }

    private double[] getCoordinatesFromLocation(String city, String postalCode) {
        if ("madrid".equalsIgnoreCase(city)) {
            return new double[]{40.4168, -3.7038}; // Coordenadas de Madrid
        }
        throw new IllegalArgumentException("Ubicación no encontrada");
    }

}
