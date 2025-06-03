package com.help.community.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una solicitud de ayuda.
 * Incluye información básica y usuarios involucrados (creador y voluntario).
 */
@Data
@Builder
public class RequestResponseDTO {

    // Información básica de la solicitud
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    // Usuarios involucrados
    private UserDTO creator;
    private UserDTO volunteer;

    // Ubicación
    private Double latitude;
    private Double longitude;

    // Información de ruta
    private Double travelDistance;      // en metros
    private Double travelDuration;      // en segundos
    private Boolean reachable;          // si el voluntario puede llegar a tiempo
    private String transportMode;      // WALKING, CAR, BICYCLE, etc.
    private String transportModeLabel;  // "A pie", "En coche", etc. (para UI)
    private Long timeToDeadline;       // segundos restantes hasta el deadline

    // Métodos auxiliares para el frontend
    public String getFormattedDistance() {
        if (travelDistance == null) return "N/A";
        return travelDistance > 1000 ?
                String.format("%.1f km", travelDistance/1000) :
                String.format("%.0f m", travelDistance);
    }

    public String getFormattedDuration() {
        if (travelDuration == null) return "N/A";
        long hours = (long) (travelDuration / 3600);
        long minutes = (long) ((travelDuration % 3600) / 60);
        return hours > 0 ?
                String.format("%dh %02dm", hours, minutes) :
                String.format("%dm", minutes);
    }

    @Builder.Default
    private Boolean allowsCarTransport = false; // Si la solicitud acepta transporte en coche
}
