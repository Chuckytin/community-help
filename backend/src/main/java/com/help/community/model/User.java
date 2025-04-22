package com.help.community.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Representa a un usuario en el sistema.
 * Esta entidad se mapea a la tabla "users" en la BBDD y almacena información de autenticación y perfil del usuario.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Request> createdRequests;

    public User() {}

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = "ROLE_USER";
    }

}
