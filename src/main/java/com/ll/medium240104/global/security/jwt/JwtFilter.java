package com.ll.medium240104.global.security.jwt;

import com.ll.medium240104.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium240104.global.security.jwt.refreshToken.service.RefreshTokenService;
import com.ll.medium240104.global.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtils.getJwtFromRequest(request);

        if (token != null) {
            try {
                Jws<Claims> jws = jwtUtils.validateJwtToken(token);

                UserDetails user = userDetailsService.loadUserByUsername(jws.getPayload().getSubject());

                Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                if (e instanceof ExpiredJwtException) {
                    String refreshTokenStr = jwtUtils.getRefreshFromRequest(request);

                    RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                            .orElseGet(() -> null);

                    if (refreshToken != null && refreshToken.getExpiryDate().isAfter(Instant.now())) {
                        String jwt = jwtUtils.generateJwtToken(refreshToken.getMember().getEmail());

                        ResponseCookie jwtCookie = jwtUtils.createJwtCookie(jwt);

                        response.addHeader("Set-Cookie", jwtCookie.toString());
                    } else {
                        response.addHeader("Set-Cookie", jwtUtils.cleanJwtCookie().toString());
                        response.addHeader("Set-Cookie", jwtUtils.cleanRefreshCookie().toString());

                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
