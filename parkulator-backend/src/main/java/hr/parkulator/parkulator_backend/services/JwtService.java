package hr.parkulator.parkulator_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import hr.parkulator.parkulator_backend.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.*;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    private final SecretKey key;

    //JWT signing key loaded from application properties
    public JwtService(@Value("${jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user){
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("userId", user.getId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key)
            .compact();
    }

    public String extractEmail(Claims claims) {
        return claims.getSubject();
    }

    public Long extractUserId(Claims claims) {
        Object value = claims.get("userId");
        return value == null ? null : Long.valueOf(value.toString());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public boolean isTokenValid(Claims claims, UserDetails userDetails) {
        String email = extractEmail(claims);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }


}
