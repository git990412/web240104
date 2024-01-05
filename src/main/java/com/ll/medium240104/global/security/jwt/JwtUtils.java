package com.ll.medium240104.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    private final SecretKey secret;

    public JwtUtils() {
        secret = Jwts.SIG.HS512.key().build();
    }

    public String generateJwtToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(secret)
                .compact();
    }

    public Jws<Claims> validateJwtToken(String authToken) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(authToken);
    }
}
