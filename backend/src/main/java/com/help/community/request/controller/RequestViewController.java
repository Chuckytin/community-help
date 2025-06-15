package com.help.community.request.controller;

import com.help.community.core.exception.ResourceNotFoundException;
import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.request.dto.RequestResponseDTO;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import com.help.community.request.service.RequestService;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/request")
public class RequestViewController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestService requestService;

    public RequestViewController(RequestRepository requestRepository,
                                 UserRepository userRepository,
                                 RequestService requestService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestService = requestService;
    }

    @GetMapping("/new")
    public String newRequestForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        model.addAttribute("user", user);
        return "new-request";
    }

    @GetMapping("/{requestId}")
    public String viewRequest(@PathVariable Long requestId,
                              @AuthenticationPrincipal Object principal,
                              Model model) {

        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (!request.getCreator().equals(user)) {
            throw new AccessDeniedException("No tienes permiso para ver esta solicitud");
        }

        model.addAttribute("request", requestService.toDTO(request));
        model.addAttribute("user", user);
        return "request-detail";
    }

    @GetMapping
    public String myRequests(@AuthenticationPrincipal Object principal, Model model) {
        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<RequestResponseDTO> requests = user.getCreatedRequests().stream()
                .map(requestService::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("requests", requests);
        model.addAttribute("user", user);
        return "my-requests";
    }

    private String getUsernameFromPrincipal(Object principal) {
        if (principal instanceof CustomOAuth2User oauthUser) {
            return oauthUser.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new AccessDeniedException("Usuario no autenticado");
    }

    @GetMapping("/requests")
    public String showMyRequests(@AuthenticationPrincipal Object principal, Model model) {
        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<RequestResponseDTO> requests = requestService.getMyRequests(principal);

        model.addAttribute("requests", requests);
        model.addAttribute("user", user);
        return "my-requests";
    }

}