package com.ll.medium240104.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJwtUtil {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TestRestTemplate restTemplate;

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

    @Test
    @DisplayName("Jwt 쿠키 전송 테스트")
    void t3() {
        String email = "wjdwn282@gmail.com";
        String token = jwtUtils.generateJwtToken(email);

        ResponseCookie jwtCookie = jwtUtils.createJwtCookie(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", jwtCookie.getName() + "=" + jwtCookie.getValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/validate", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
