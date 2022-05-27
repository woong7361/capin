package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.config.exception.customexception.token.RefreshTokenException;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hanghae.finalp.util.JwtTokenUtils.CLAIM_ID;
import static com.hanghae.finalp.util.JwtTokenUtils.TOKEN_NAME_WITH_SPACE;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;

    /**
     * refreshToken으로 accessToken 발급
     */
    public MemberDto.refreshTokenRes createAccessTokenByRefreshToken(String refreshToken) {
        Long memberId = jwtTokenUtils.verifyToken(refreshToken.replace(TOKEN_NAME_WITH_SPACE, ""))
                .getClaim(CLAIM_ID).asLong();
        String inRedisToken = redisUtils.getRefreshTokenData(memberId.toString());

        log.debug("custom log:: in redis refresh token {}", inRedisToken);
        if(!refreshToken.equals(inRedisToken)) throw new RefreshTokenException();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotExistException::new);
        String accessToken = jwtTokenUtils.createAccessToken(memberId, member.getUsername());

        return new MemberDto.refreshTokenRes(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     */
    public void logout(Long memberId) {
        redisUtils.deleteRefreshTokenData(memberId.toString());
    }
}
