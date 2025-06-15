package com.help.community.home.controller;

import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.core.security.service.JwtService;
import com.help.community.home.dto.DtoConverter;
import com.help.community.request.dto.RequestNearbyDTO;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import com.help.community.request.service.RequestService;
import com.help.community.user.dto.UserProfileDTO;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import com.help.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final UserService userService;
    private final DtoConverter dtoConverter;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String home(Model model,
                       @AuthenticationPrincipal Object principal,
                       @CookieValue(value = "token", required = false) String token) {

        User user = null;
        Map<String, Object> attributes = new HashMap<>();

        if (principal instanceof CustomOAuth2User oauthUser) {
            user = userRepository.findByEmail(oauthUser.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            attributes = oauthUser.getAttributes();
        } else if (principal instanceof UserDetails userDetails) {
            user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        } else if (principal instanceof String email) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        } else if (token != null) {
            try {
                String email = jwtService.extractUsername(token);
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            } catch (Exception e) {
                return "redirect:/login";
            }
        }

        if (user == null) {
            return "redirect:/login";
        }

        try {
            UserProfileDTO userProfile = userService.getUserProfile(user.getUserId());
            model.addAttribute("user", dtoConverter.convertToUserDto(user));
            model.addAttribute("authAttributes", attributes);

            // Añadir las solicitudes del usuario al modelo
            List<Request> userRequests = requestRepository.findByCreatorOrderByCreatedAtDesc(user);
            model.addAttribute("userRequests", userRequests.stream()
                    .map(requestService::toDTO)
                    .collect(Collectors.toList()));

            if (userProfile.getLatitude() != null && userProfile.getLongitude() != null) {
                List<RequestNearbyDTO> nearbyRequests = requestService.findNearbyRequests(
                        userProfile.getLatitude(),
                        userProfile.getLongitude(),
                        10000.0
                );
                model.addAttribute("requests", nearbyRequests);
            } else {
                model.addAttribute("requests", List.of());
                model.addAttribute("warning", "Actualiza tu ubicación para ver solicitudes cercanas");
            }

            return "home";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "home";
        }
    }
}