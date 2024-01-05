package com.ll.medium240104.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJwtUtil {
    @Autowired
    JwtUtils jwtUtils;

    @Test
    @DisplayName("토큰생성 테스트")
    void t1() {
        String token = jwtUtils.generateJwtToken("wjdwn282@gmail.com");

        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("토큰검증 테스트")
    void t2() {
        String email = "wjdwn282@gmail.com";
        String token = jwtUtils.generateJwtToken(email);

        try {
            Jws<Claims> jws = jwtUtils.validateJwtToken(token);
            assertThat(jws.getPayload().getSubject()).isEqualTo(email);
        } catch (JwtException e) {
            assertThat(false).isTrue();
        }
    }
}
