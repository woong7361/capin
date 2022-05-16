package com.hanghae.finalp.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae.finalp.dto.LoginDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hanghae.finalp.util.JwtTokenUtils.CLAIM_ID;
import static com.hanghae.finalp.util.JwtTokenUtils.TOKEN_NAME_WITH_SPACE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {

    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;

    public LoginDto.refreshTokenRes createAccessTokenByRefreshToken(String refreshToken) {
        refreshToken = refreshToken.replace(TOKEN_NAME_WITH_SPACE, "");
        DecodedJWT decodedJWT = jwtTokenUtils.verifyToken(refreshToken);
        Long memberId = decodedJWT.getClaim(CLAIM_ID).asLong();
        String inRedisToken = redisUtils.getData(memberId.toString());
        if(!refreshToken.equals(inRedisToken)) throw new RuntimeException("not valid refresh token");

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("not exist nmember"));

        String accessToken = jwtTokenUtils.createAccessToken(memberId, member.getUsername());

        return new LoginDto.refreshTokenRes(accessToken, refreshToken);
    }

    public void logout(Long memberId) {
        redisUtils.deleteData(memberId.toString());
    }
}
