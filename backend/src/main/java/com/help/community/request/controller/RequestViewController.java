package com.help.community.request.controller;

import com.help.community.user.model.User;
import com.help.community.user.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/request")
public class RequestViewController {

    private final UserRepository userRepository;

    public RequestViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String newRequestForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Obtener el usuario autenticado
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        model.addAttribute("user", user);
        return "new-request";
    }

    @GetMapping("/{requestId}")
    public String viewRequest(@PathVariable Long requestId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        model.addAttribute("user", user);
        model.addAttribute("requestId", requestId);
        return "request-created";
    }
}