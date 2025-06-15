package com.help.community.core.security.web;

import org.springframework.ui.Model;
import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import com.help.community.user.dto.UserDTO;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

//    @GetMapping("/home")
//    public String home(Model model, @AuthenticationPrincipal CustomOAuth2User oauthUser) {
//        User user = userRepository.findByEmail(oauthUser.getEmail())
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//
//        UserDTO userDTO = UserDTO.builder()
//                .name(user.getName())
//                .email(user.getEmail())
//                .provider(user.getProvider())
//                .latitude((user.getLocation() != null) ? user.getLocation().getY() : null)
//                .longitude((user.getLocation() != null) ? user.getLocation().getX() : null)
//                .attributes(oauthUser.getAttributes())
//                .build();
//
//        model.addAttribute("user", userDTO);
//
//        // Solo buscar solicitudes si el usuario tiene ubicación
//        if (user.getLocation() != null) {
//            List<Request> nearbyRequests = requestRepository.findNearbyPendingRequests(
//                    user.getLocation().getY(),
//                    user.getLocation().getX(),
//                    5000
//            );
//            model.addAttribute("requests", nearbyRequests);
//        } else {
//            model.addAttribute("requests", List.of());
//        }
//
//        return "home";
//    }
}