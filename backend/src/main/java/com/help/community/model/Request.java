package com.help.community.model;

import jakarta.persistence.*;
import lombok.Data;

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
    private String category; //TODO: Ej: Emergencia, Mantenimiento, Psicología...

    @Column(nullable = false)
    private String status = "PENDIENTE"; //TODO: "PENDIENTE", "ACEPTADA", "COMPLETADA"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id")
    private User volunteer; //TODO: opcional

    public Request() {}

    public Request(String title, String description, String category, User creator) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.creator = creator;
    }
}
