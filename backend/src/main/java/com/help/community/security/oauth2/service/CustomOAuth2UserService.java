package com.help.community.security.oauth2.service;

import com.help.community.model.User;
import com.help.community.repository.UserRepository;
import com.help.community.security.oauth2.model.GoogleOAuth2UserInfo;
import com.help.community.security.oauth2.OAuth2UserInfo;
import com.help.community.security.oauth2.model.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Servicio personalizado para manejar la autenticación de usuarios mediante OAuth2.
     * Extiende el comportamiento por defecto de Spring para incluir persistencia y asignación de roles.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    /**
     * Procesa la información del usuario OAuth2, registrando uno nuevo si no existe.
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> registerNewUser(provider, userInfo));

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    /**
     * Registra un nuevo usuario en la base de datos utilizando los datos del proveedor OAuth2.
     */
    private User registerNewUser(String provider, OAuth2UserInfo userInfo) {
        User newUser = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .roles(Set.of(User.Role.ROLE_USER))
                .provider(provider)
                .providerId(userInfo.getId())
                .phoneNumber(null)
                .build();

        return userRepository.save(newUser);
    }
}
