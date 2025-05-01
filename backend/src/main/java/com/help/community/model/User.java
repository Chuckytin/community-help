package com.help.community.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();

    public enum Role {
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Request> createdRequests;

    /**
     * Rol por defecto de Usuario
     */
    private void initDefaultRoles() {
        this.roles.add(Role.ROLE_USER);
    }

    public User() {
        initDefaultRoles();
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        initDefaultRoles();
    }

    /**
     * Para obtener el primer rol
     */
    public String getMainRole() {
        return this.roles.stream()
                .findFirst()
                .map(Enum::name)
                .orElse(Role.ROLE_USER.name());
    }

}