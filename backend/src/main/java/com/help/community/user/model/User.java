package com.help.community.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.help.community.request.model.Request;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.locationtech.jts.geom.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a un usuario en el sistema.
 * Esta entidad se mapea a la tabla "users" en la BBDD y almacena información de autenticación y perfil del usuario.
 */
@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(columnDefinition = "geography(Point,4326)")
    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    private Point location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_USER));

    public enum Role {
        ROLE_USER,
        ROLE_MODERATOR,
        ROLE_ADMIN
    }

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Request> createdRequests;

    @OneToMany(mappedBy = "volunteer")
    private List<Request> volunteeredRequests;

    @Column(name = "provider")
    private String provider = "local";

    @Column(name = "provider_id")
    private String providerId;

    public User() { }

    public User(String email, String name, String password) {
        this();
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public User(Long userId, String email, String name, String phoneNumber,
                Set<Role> roles, Long createdRequestsCount) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public void setLocationFromLatLon(double latitude, double longitude) {
        this.location = new GeometryFactory().createPoint(new Coordinate(longitude, latitude));
        this.location.setSRID(4326);
    }

    public Double getLatitude() {
        return (location != null) ? location.getY() : null;
    }

    public Double getLongitude() {
        return (location != null) ? location.getX() : null;
    }

    /**
     * Obtiene el rol principal del usuario.
     */
    public String getMainRole() {
        return this.roles.stream()
                .findFirst()
                .map(Enum::name)
                .orElse(Role.ROLE_USER.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roles == null) {
            return List.of();
        }

        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}