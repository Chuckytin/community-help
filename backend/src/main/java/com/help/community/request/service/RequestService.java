package com.help.community.request.service;

import com.help.community.request.dto.RequestNearbyDTO;
import com.help.community.request.dto.RequestResponseDTO;
import com.help.community.user.dto.UserDTO;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
     * Obtiene todas las solicitudes SIN paginación (para compatibilidad con endpoints existentes).
     * @deprecated Usar mejor getAllRequests(Pageable pageable) para nuevos desarrollos.
     */
    public List<RequestResponseDTO> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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

        return RequestResponseDTO.builder()
                .id(request.getRequest_id())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .deadline(request.getDeadline())
                .creator(creatorDTO)
                .volunteer(volunteerDTO)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public List<RequestNearbyDTO> findNearbyRequests(Double latitude, Double longitude, Double radius) {
        return requestRepository.findNearbyRequests(latitude, longitude, radius)
                .stream()
                .map(request -> toNearbyDTO(request, latitude, longitude))
                .collect(Collectors.toList());
    }

    private RequestNearbyDTO toNearbyDTO(Request request, Double userLat, Double userLon) {
        return RequestNearbyDTO.builder()
                .id(request.getRequest_id())
                .title(request.getTitle())
                .category(request.getCategory())
                .location(request.getLocation())
                .distance(calculateDistance(request.getLatitude(), request.getLongitude(), userLat, userLon))
                .build();
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000;
    }

}