package com.hanghae.finalp.config.security.filter;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByKakaoId(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 요청한 회원이 DB에 없다"));

        return new PrincipalDetails(member.getId(), member.getUsername());
    }
}
