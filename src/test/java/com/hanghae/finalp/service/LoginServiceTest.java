package com.hanghae.finalp.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.config.exception.customexception.token.RefreshTokenException;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

import java.util.Optional;

import static com.hanghae.finalp.util.JwtTokenUtils.CLAIM_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks LoginService loginService;

    @Mock JwtTokenUtils jwtTokenUtils;
    @Mock RedisUtils redisUtils;
    @Mock MemberRepository memberRepository;

    private Member member1;
    private Member member2;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
    }

    @Nested
    class createAccessTokenByRefreshToken {

        @Test
        public void 성공() throws Exception{
            //given
            DecodedJWT mock = Mockito.mock(DecodedJWT.class);
            mock.getClaim(CLAIM_ID);
            given(jwtTokenUtils.verifyToken(anyString())).willReturn(mock);
            Claim mock1 = Mockito.mock(Claim.class);
            given(mock.getClaim(anyString())).willReturn(mock1);
            given(mock1.asLong()).willReturn(1L);

            given(redisUtils.getRefreshTokenData(anyString())).willReturn("Bearer token");
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            given(jwtTokenUtils.createAccessToken(anyLong(), anyString())).willReturn("new refresh token");
            //when
            loginService.createAccessTokenByRefreshToken("Bearer token");
            //then
        }

        @Test
        public void Redis_비교_실패() throws Exception {
            //given
            DecodedJWT mock = Mockito.mock(DecodedJWT.class);
            mock.getClaim(CLAIM_ID);
            given(jwtTokenUtils.verifyToken(anyString())).willReturn(mock);
            Claim mock1 = Mockito.mock(Claim.class);
            given(mock.getClaim(anyString())).willReturn(mock1);
            given(mock1.asLong()).willReturn(1L);

            given(redisUtils.getRefreshTokenData(anyString())).willReturn("Bearer token");
            //when //then

            assertThatThrownBy(() -> loginService.createAccessTokenByRefreshToken("invalid token"))
                    .isInstanceOf(RefreshTokenException.class);
        }

        @Test
        public void entity가_존재X_실패() throws Exception {
            //given
            DecodedJWT mock = Mockito.mock(DecodedJWT.class);
            mock.getClaim(CLAIM_ID);
            given(jwtTokenUtils.verifyToken(anyString())).willReturn(mock);
            Claim mock1 = Mockito.mock(Claim.class);
            given(mock.getClaim(anyString())).willReturn(mock1);
            given(mock1.asLong()).willReturn(1L);

            given(redisUtils.getRefreshTokenData(anyString())).willReturn("Bearer token");
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when //then

            assertThatThrownBy(() -> loginService.createAccessTokenByRefreshToken("Bearer token"))
                    .isInstanceOf(MemberNotExistException.class);
        }

    }

}