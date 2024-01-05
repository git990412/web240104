package com.ll.medium240104.global.security.service;

import com.ll.medium240104.domain.member.member.entity.Member;
import com.ll.medium240104.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberService.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with email: " + username));

        UserDetailsImpl user = new UserDetailsImpl();

        user.setId(member.getId());
        user.setUsername(member.getUsername());
        user.setEmail(member.getEmail());
        user.setPassword(member.getPassword());

        return user;
    }
}
