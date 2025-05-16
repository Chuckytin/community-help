package com.help.community.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;

public class TokenGenerator {

    @Value("${jwt.secret-key}")
    private static String secretKey;

    public static void main(String[] args) {

        String token = Jwts.builder()
                .subject("test@example.com")
                .claim("name", "John Doe")
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
                .compact();

        System.out.println("Token válido: " + token);
    }

}
