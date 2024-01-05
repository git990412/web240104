package com.ll.medium240104.global.security.jwt.refreshToken.repository;

import com.ll.medium240104.domain.member.member.entity.Member;
import com.ll.medium240104.global.security.jwt.refreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    int deleteByMember(Member member);

    Optional<RefreshToken> findByMemberId(Long memberId);
}
