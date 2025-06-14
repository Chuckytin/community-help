package com.help.community.core.security.oauth2.handler;

import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.core.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador que se ejecuta después de una autenticación OAuth2 exitosa.
 * Genera un JWT y redirige al frontend con el token.
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(oauthUser);

        Cookie authCookie = new Cookie("AUTH_TOKEN", jwtToken);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true);
        authCookie.setPath("/");
        response.addCookie(authCookie);

        response.sendRedirect("/home");
    }
}