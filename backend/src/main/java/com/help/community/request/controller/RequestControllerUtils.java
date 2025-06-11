package com.help.community.request.controller;

import com.help.community.request.dto.RequestResponseDTO;
import com.help.community.request.model.Request;
import com.help.community.integration.OpenRouteService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Útiles auxiliares
 */
@Component
public class RequestControllerUtils {

    private final OpenRouteService openRouteService;

    public RequestControllerUtils(OpenRouteService openRouteService) {
        this.openRouteService = openRouteService;
    }

    /**
     * Parsea un string a un modo de transporte soportado.
     */
    public OpenRouteService.TransportMode parseTransportMode(String transportMode) {
        if (transportMode == null) {
            return OpenRouteService.TransportMode.FOOT_WALKING;
        }
        return switch (transportMode.toLowerCase()) {
            case "car", "driving" -> OpenRouteService.TransportMode.DRIVING_CAR;
            case "bike", "cycling" -> OpenRouteService.TransportMode.CYCLING;
            default -> OpenRouteService.TransportMode.FOOT_WALKING;
        };
    }

    /**
     * Calcula si una solicitud es alcanzable desde la ubicación del usuario
     * y actualiza el DTO con distancia, duración y modo de transporte.
     */
    public void calculateReachability(double userLat, double userLon,
                                       Request request, RequestResponseDTO dto,
                                       OpenRouteService.TransportMode mode) {

        try {
            OpenRouteService.TravelTimeResponse travelInfo = openRouteService.getTravelTime(
                    userLat, userLon,
                    request.getLatitude(), request.getLongitude(),
                    mode
            );

            dto.setTravelDistance(travelInfo.getDistance());
            dto.setTravelDuration(travelInfo.getDuration());
            dto.setTransportMode(mode.name());
            dto.setTransportModeLabel(getTransportLabel(mode));

            if (request.getDeadline() != null) {
                long remainingTime = Duration.between(LocalDateTime.now(), request.getDeadline()).getSeconds();
                dto.setReachable(travelInfo.getDuration() <= remainingTime);
                dto.setTimeToDeadline(remainingTime);
            }
        } catch (Exception e) {
            dto.setTransportMode(mode.name() + "_ERROR");
            dto.setReachable(false);
        }
    }

    /**
     * Devuelve una etiqueta legible para un modo de transporte.
     */
    public String getTransportLabel(OpenRouteService.TransportMode mode) {
        return switch (mode) {
            case DRIVING_CAR -> "En coche";
            case CYCLING -> "En bicicleta";
            default -> "A pie";
        };
    }

    /**
     * Valida si unas coordenadas son válidas (latitud y longitud).
     */
    public boolean isValidCoordinate(double lat, double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    /**
     * Calcula la distancia entre dos coordenadas geográficas usando la fórmula de Haversine.
     */
    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c * 1000;
    }

}
