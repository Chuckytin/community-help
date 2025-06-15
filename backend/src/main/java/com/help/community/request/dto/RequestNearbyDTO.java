package com.help.community.request.dto;

import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
@Builder
public class RequestNearbyDTO {
    private Long id;
    private String title;
    private String category;
    private Double distance;
    private Point location;
    private Double travelDuration;

    public Double getX() {
        return location != null ? location.getX() : null;
    }

    public Double getY() {
        return location != null ? location.getY() : null;
    }
}