package com.help.community.core.security.oauth2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> oauth2Success(
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType", "Bearer",
                "message", "Autenticación exitosa"
        ));
    }
}
