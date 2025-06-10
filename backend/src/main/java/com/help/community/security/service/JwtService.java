package com.help.community.security.service;

import com.help.community.security.jwt.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la gestión de tokens JWT:
 * - Generación de tokens
 * - Validación y extracción de datos del token
 * - Configuración centralizada con JwtProperties
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Genera un token JWT simple sin claims personalizados para un UserDetails.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * Genera token JWT con claims personalizados
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtProperties.getExpiration());
    }

    /**
     *
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Verifica que el token pertenece al usuario y no ha expirado.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario (subject) del token.
     */
    public String extractUsername(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Verifica si el token ha expirado.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration == null || expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae cualquier claim del token usando una función resolutora.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del JWT.
     */
    private Claims extractAllClaims(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expirated", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid sign", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed token", e);
        }
    }

    /**
     * Convierte la clave secreta en un objeto Key compatible con HS256.
     *
     * @return clave de firma.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Renueva un token JWT si está dentro del periodo de gracia.
     * Si el token ha expirado recientemente, se genera uno nuevo con los mismos claims.
     *
     * @param token Token JWT a renovar
     * @return Nuevo token JWT
     * @throws JwtException si el token es inválido o demasiado antiguo
     */
    public String refreshToken(String token) throws JwtException {
        try {
            final Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration != null &&
                    expiration.before(new Date(System.currentTimeMillis() - jwtProperties.getRefreshGracePeriod()))) {
                throw new JwtException("Token too old to refresh.");
            }

            return rebuildToken(claims);
        } catch (ExpiredJwtException e) {
            return rebuildToken(e.getClaims());
        }
    }

    /**
     * Reconstruye un token JWT con los claims proporcionados.
     * Genera un nuevo token con la misma información pero con nueva fecha de emisión
     * y firma.
     *
     * @param claims Los claims que contendrá el nuevo token
     * @return El nuevo token JWT firmado y codificado
     */
    private String rebuildToken(Claims claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

}
