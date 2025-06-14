package com.help.community.core.security.oauth2.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

//    @GetMapping("/success")
//    public ResponseEntity<Void> oauth2Success(
//            @RequestParam("token") String token,
//            HttpServletResponse response
//    ) throws IOException {
//        response.sendRedirect("/home?token=" + token);
//        return ResponseEntity.ok().build();
//    }
}
