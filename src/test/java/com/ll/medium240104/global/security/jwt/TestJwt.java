package com.ll.medium240104.global.security.jwt;

import com.ll.medium240104.domain.member.member.entity.Member;
import com.ll.medium240104.domain.member.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJwt {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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
        memberRepository.save(Member.builder()
                .username("test")
                .email("wjdwn282@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .build());

        String email = "wjdwn282@gmail.com";
        String token = jwtUtils.generateJwtToken(email);

        ResponseCookie jwtCookie = jwtUtils.createJwtCookie(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", jwtCookie.getName() + "=" + jwtCookie.getValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/validate", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("jwtFilter 작동 테스트")
    void t4() {
        String email = "wjdwn282@gmail.com";
        String token = jwtUtils.generateJwtToken(email);

        ResponseCookie jwtCookie = jwtUtils.createJwtCookie(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", jwtCookie.getName() + "=" + jwtCookie.getValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/filterTest", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("jwtFilter 만료 테스트")
    void t5() throws InterruptedException {
        String email = "wjdwn282@gmail.com";

        String token = jwtUtils.generateJwtTokenWithMs(email, 1000);

        Thread.sleep(1500);

        ResponseCookie jwtCookie = jwtUtils.createJwtCookie(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", jwtCookie.getName() + "=" + jwtCookie.getValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/filterTest", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }
}
