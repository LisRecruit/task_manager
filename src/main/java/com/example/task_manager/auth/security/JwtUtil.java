package com.example.task_manager.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return extractClaim(token, claims -> claims.get("user_id", Long.class));
//        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
//        return extractClaim(jwt, claims -> claims.get("user_id", Long.class));
    }



    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken (UserDetails userDetails, Long userId) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        System.out.printf("roles: %s\n", roles);
        claims.put("roles", roles);
        System.out.println("Roles in token: " + userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    Boolean isTokenExpired(String token) {
        try {
            Date expiredAt = extractExpiration(token);
            long currentTime = System.currentTimeMillis();
            long expirationTime = expiredAt.getTime();
            long allowedClockSkewMillis = 1;
            return expirationTime < (currentTime - allowedClockSkewMillis);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }


    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();

    }

    private Claims getAllClaimsFromToken(String token) {
           return Jwts.parser()
                   .verifyWith(key)
                   .setAllowedClockSkewSeconds(5)
                   .build()
                   .parseClaimsJws(token)
                   .getPayload();
    }
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }






}
