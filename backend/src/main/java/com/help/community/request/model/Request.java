package com.help.community.request.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.help.community.user.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.hibernate.type.SqlTypes;


import java.time.LocalDateTime;

/**
 * Representa una solicitud de ayuda publicada por un usuario.
 * Incluye categoría, estado y fechas para gestionar el ciclo de vida de la solicitud en la comunidad.
 */
@Data
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long request_id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String status = "PENDIENTE";

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    @Column(columnDefinition = "geography(Point,4326)")
    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    private Point location;

    private LocalDateTime deadline;

    public Request() {}

    public Request(String title, String description, String category, User creator) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.creator = creator;
    }

    public void setLocationFromLatLon(double latitude, double longitude) {
        this.location = new GeometryFactory().createPoint(new Coordinate(longitude, latitude));
        this.location.setSRID(4326);
    }

    public double getLatitude() {
        return location != null ? location.getY() : 0;
    }

    public double getLongitude() {
        return location != null ? location.getX() : 0;
    }
}
