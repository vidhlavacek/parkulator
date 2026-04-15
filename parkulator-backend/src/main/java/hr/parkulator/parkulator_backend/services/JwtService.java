package hr.parkulator.parkulator_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hr.parkulator.parkulator_backend.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.*;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("id", user.getId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60))
            .signWith(key) 
            .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("id", Integer.class)
        );
    }
}
