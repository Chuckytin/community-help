package com.help.community.core.security.oauth2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.help.community.request.model.Request;
import com.help.community.user.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.locationtech.jts.geom.Point;

import java.util.*;

/**
 * Representa un usuario autenticado a través de OAuth2
 */
public class CustomOAuth2User extends User implements OAuth2User {

    @Getter
    private final User user;
    private final Map<String, Object> attributes;

    /**
     * Crea una instancia de CustomOAuth2User con los datos del usuario local y atributos de OAuth2.
     */
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /**
     * Obtiene los atributos proporcionados por el proveedor OAuth2.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail(); // o user.getName() según tu necesidad
    }

    // Métodos delegados del User
    public Long getUserId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFullName() {
        return user.getName();
    }

    public String getPassword() {
        return user.getPassword();
    }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public Point getLocation() {
        return user.getLocation();
    }

    public Set<Role> getRoles() {
        return user.getRoles();
    }

    public String getMainRole() {
        return user.getMainRole();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    // Métodos de ubicación
    public Double getLatitude() {
        return user.getLatitude();
    }

    public Double getLongitude() {
        return user.getLongitude();
    }

    //Métodos del provider
    public String getProvider() {
        return user.getProvider();
    }

    public String getProviderId() {
        return user.getProviderId();
    }

    // Métodos de UserDetails (seguridad)
    public String getUsername() {
        return user.getUsername();
    }

    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    public boolean isEnabled() {
        return user.isEnabled();
    }

    // Métodos para obtener requests creados por el usuario
    @JsonIgnore
    @Override
    public List<Request> getCreatedRequests() {
        return super.getCreatedRequests();
    }

    public Long getCreatedRequestsCount() {
        return user.getCreatedRequests() != null ? user.getCreatedRequests().size() : 0L;
    }

    // Métodos para obtener requests donde el usuario es voluntario
    @JsonIgnore
    @Override
    public List<Request> getVolunteeredRequests() {
        return super.getVolunteeredRequests();
    }

    public int getVolunteeredRequestsCount() {
        return user.getVolunteeredRequests() != null ? user.getVolunteeredRequests().size() : 0;
    }

    // Métodos para verificar roles/estados
    public boolean hasPendingRequests() {
        return user.getCreatedRequests() != null &&
                user.getCreatedRequests().stream()
                        .anyMatch(r -> "PENDIENTE".equals(r.getStatus()));
    }

    public boolean isVolunteerInAnyRequest() {
        return user.getVolunteeredRequests() != null &&
                !user.getVolunteeredRequests().isEmpty();
    }

    // Métodos para control de acceso
    public boolean canEditRequest(Long requestId) {
        return user.getCreatedRequests() != null &&
                user.getCreatedRequests().stream()
                        .anyMatch(r -> r.getRequest_id().equals(requestId));
    }

    public boolean isVolunteerForRequest(Long requestId) {
        return user.getVolunteeredRequests() != null &&
                user.getVolunteeredRequests().stream()
                        .anyMatch(r -> r.getRequest_id().equals(requestId));
    }

    public Map<String, Object> getSafeAttributes() {
        Map<String, Object> safeAttrs = new HashMap<>(attributes);
        safeAttrs.remove("sub");
        safeAttrs.remove("email_verified");
        return safeAttrs;
    }

}
