package com.help.community.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.help.community.user.dto.UserDTO;
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

    @JsonProperty("request_id")
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    private UserDTO creator;
    private UserDTO volunteer;

    private Double latitude;
    private Double longitude;

    private Double travelDistance;
    private Double travelDuration;
    private Boolean reachable;
    private String transportMode;
    private String transportModeLabel;
    private Long timeToDeadline;

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

}
