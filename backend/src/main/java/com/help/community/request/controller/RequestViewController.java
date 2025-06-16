package com.help.community.request.controller;

import com.help.community.core.exception.ResourceNotFoundException;
import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.request.dto.RequestResponseDTO;
import com.help.community.request.model.Request;
import com.help.community.request.repository.RequestRepository;
import com.help.community.request.service.RequestService;
import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequestMapping("/request")
public class RequestViewController {

    private static final Logger logger = LoggerFactory.getLogger(RequestViewController.class);

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
        if (requestId == null || requestId <= 0) {
            logger.error("Invalid requestId: {}", requestId);
            throw new ResourceNotFoundException("ID de solicitud inválido");
        }

        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        RequestResponseDTO requestDTO = requestService.toDTO(request, user);
        boolean isCreator = request.getCreator().equals(user);

        model.addAttribute("request", requestDTO);
        model.addAttribute("user", user);
        model.addAttribute("isCreator", isCreator);
        return "request-detail";
    }

    @GetMapping
    public String myRequests(@AuthenticationPrincipal Object principal, Model model) {
        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<RequestResponseDTO> requests = requestService.getMyRequests(principal);
        model.addAttribute("requests", requests);
        model.addAttribute("user", user);
        return "my-requests";
    }

    // Para solicitudes aceptadas
    @GetMapping("/accepted/{id}")
    public String showAcceptedRequest(@PathVariable Long id, Model model) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        model.addAttribute("request", requestService.toDTO(request));
        model.addAttribute("isAccepted", true);
        return "request-accepted";
    }

    @GetMapping("/requests")
    public String showMyRequests(@AuthenticationPrincipal Object principal, Model model) {
        return myRequests(principal, model);
    }

    @GetMapping("/edit/{requestId}")
    public String editRequestForm(@PathVariable Long requestId,
                                  @AuthenticationPrincipal Object principal,
                                  Model model) {
        String username = getUsernameFromPrincipal(principal);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (!request.getCreator().equals(user)) {
            throw new AccessDeniedException("No tienes permiso para editar esta solicitud");
        }

        model.addAttribute("request", requestService.toDTO(request, user));
        model.addAttribute("user", user);
        return "edit-request";
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
}