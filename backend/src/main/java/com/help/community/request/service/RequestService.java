package com.help.community.request.service;

import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.integration.OpenRouteService;
import com.help.community.request.controller.RequestController;
import com.help.community.request.dto.RequestNearbyDTO;
import com.help.community.request.dto.RequestResponseDTO;
import com.help.community.user.dto.UserDTO;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Service para transformar las entidades DTOs, centraliza la lógica de mapeo.
 * Convierte una entidad Request a su DTO y mapea el creator al UserDTO.
 */
@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final OpenRouteService openRouteService;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository, OpenRouteService openRouteService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.openRouteService = openRouteService;
    }

    public List<RequestResponseDTO> getMyRequests(Object principal) {
        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return user.getCreatedRequests().stream()
                .map(request -> toDTO(request, user))
                .collect(Collectors.toList());
    }

    public Page<RequestResponseDTO> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable)
                .map(request -> toDTO(request, null));
    }

    public RequestResponseDTO toDTO(Request request, User viewer) {
        if (request.getRequest_id() == null) {
            logger.error("Found request with null ID in toDTO: {}", request);
        }
        UserDTO creatorDTO = UserDTO.builder()
                .id(request.getCreator().getUserId())
                .name(request.getCreator().getName())
                .phoneNumber(request.getCreator().getPhoneNumber())
                .build();

        UserDTO volunteerDTO = null;
        if (request.getVolunteer() != null) {
            volunteerDTO = UserDTO.builder()
                    .id(request.getVolunteer().getUserId())
                    .name(request.getVolunteer().getName())
                    .role(request.getVolunteer().getMainRole())
                    .build();
        }

        String statusClass = switch (request.getStatus()) {
            case "PENDIENTE" -> "bg-warning";
            case "ACEPTADO" -> "bg-success";
            default -> "bg-secondary";
        };

        // Calculate distance and duration if viewer location is available
        Double travelDistance = null;
        Double travelDuration = null;
        if (viewer != null && viewer.getLatitude() != null && viewer.getLongitude() != null
                && request.getLatitude() != 0 && request.getLongitude() != 0) {
            try {
                OpenRouteService.TravelTimeResponse travelResponse = openRouteService.getTravelTime(
                        viewer.getLatitude(), viewer.getLongitude(),
                        request.getLatitude(), request.getLongitude(),
                        OpenRouteService.TransportMode.DRIVING_CAR
                );
                travelDistance = travelResponse.getDistance(); // Meters
                travelDuration = travelResponse.getDuration(); // Seconds
            } catch (Exception e) {
                logger.warn("Failed to calculate travel distance/duration for request {}: {}", request.getRequest_id(), e.getMessage());
            }
        }

        return RequestResponseDTO.builder()
                .id(request.getRequest_id())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus())
                .statusClass(statusClass)
                .createdAt(request.getCreatedAt())
                .deadline(request.getDeadline())
                .creator(creatorDTO)
                .volunteer(volunteerDTO)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .travelDistance(travelDistance)
                .travelDuration(travelDuration)
                .address(request.getLocation() != null ? String.valueOf(request.getLocation()) : "Ubicación no especificada")
                .build();
    }

    public RequestResponseDTO toDTO(Request request) {
        return toDTO(request, null);
    }

    public List<RequestNearbyDTO> findNearbyRequests(Double latitude, Double longitude, Double radius) {
        return requestRepository.findNearbyPendingRequests(latitude, longitude, radius)
                .stream()
                .map(request -> toNearbyDTO(request, latitude, longitude))
                .collect(Collectors.toList());
    }

    public RequestNearbyDTO toNearbyDTO(Request request, Double userLat, Double userLon) {
        Double distance = null;
        Double travelDuration = null;
        try {
            OpenRouteService.TravelTimeResponse travelResponse = openRouteService.getTravelTime(
                    userLat, userLon,
                    request.getLatitude(), request.getLongitude(),
                    OpenRouteService.TransportMode.DRIVING_CAR
            );
            distance = travelResponse.getDistance();
            travelDuration = travelResponse.getDuration();
        } catch (Exception e) {
            logger.warn("Failed to calculate travel distance/duration for nearby request {}: {}", request.getRequest_id(), e.getMessage());
        }

        return RequestNearbyDTO.builder()
                .id(request.getRequest_id())
                .title(request.getTitle())
                .category(request.getCategory())
                .location(request.getLocation())
                .distance(distance)
                .travelDuration(travelDuration)
                .build();
    }

    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof CustomOAuth2User oauthUser) {
            return oauthUser.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new UsernameNotFoundException("Usuario no autenticado");
    }
}