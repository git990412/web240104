package com.ll.medium240104.global.security.jwt.controller;

import com.ll.medium240104.global.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {
    private final JwtUtils jwtUtils;

    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromRequest(request);

        try {
            Jws<Claims> jws = jwtUtils.validateJwtToken(jwt);
            System.out.println(jws.getPayload().getSubject());

            return ResponseEntity.ok().build();
        } catch (JwtException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
