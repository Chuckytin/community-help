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

import java.util.HashMap;
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

}