package config.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mi_clave_secreta_segura";
    private final long EXPIRATION_MS = 86400000; // 1 día

    // Genera el token usando el email como identificador
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // Extrae el email (subject) del token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Verifica que el token sea válido
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (email.equals(extractedEmail)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
