package com.hanghae.finalp.service.oauth;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.config.security.kakao.KakaoProfile;
import com.hanghae.finalp.config.security.kakao.OAuthToken;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KakaoOauth {

    private final MemberRepository memberRepository;
    private final RedisUtils redisUtils;
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${spring.security.oauth2.client.registration.Kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.Kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.Kakao.authorization-grant-type}")
    private String authorizationGrantType;
    @Value("${spring.security.oauth2.client.registration.Kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.Kakao.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.security.oauth2.client.provider.Kakao.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.Kakao.user-info-uri}")
    private String userInfoUri;

    @Transactional
    public MemberDto.LoginRes login(String providerName, String code) {

        OAuthToken tokenResponse = getTokenFromKakao(code); //provider에 해당하는 카카오의 OAuthToken 얻어서
        KakaoProfile kakaoProfile = getUserProfileFromKakao(providerName, tokenResponse); //카카오 프로필 요청해서 얻고

        MemberDto.LoginRes response = saveMember(providerName, kakaoProfile);//멤버 저장

        String accessToken = jwtTokenUtils.
                createAccessToken(response.getMember().getMemberId(), response.getMember().getUsername());
        String refreshToken = jwtTokenUtils.createRefreshToken(response.getMember().getMemberId());

        //hardcoding need refactoring
        redisUtils.setRefreshTokenDataExpire(
                String.valueOf(response.getMember().getMemberId()),
                refreshToken, jwtTokenUtils.getRefreshTokenExpireTime(refreshToken)
        );

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }



    //==============================================================================================//

    private OAuthToken getTokenFromKakao(String code) {
        OAuthToken oAuthToken = WebClient.create()
                .post()
                .uri(tokenUri)
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenReqeust(code))
                .retrieve()
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(OAuthToken.class)
                .block();

        return oAuthToken;
    }

    private MultiValueMap<String, String> tokenReqeust(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", authorizationGrantType);
        formData.add("redirect_uri", redirectUri);
//        formData.add("client_secret", provider.getClientSecret());
        formData.add("client_id", clientId);
        return formData;
    }

    //카카오 프로필 얻기
    private KakaoProfile getUserProfileFromKakao(String providerName, OAuthToken tokenResponse) {

        return WebClient.create()
                .get()
                .uri(userInfoUri)
                .headers(header -> header.setBearerAuth(tokenResponse.getAccess_token()))
                .retrieve()
                .bodyToMono(KakaoProfile.class)
                .block();
    }

    private MemberDto.LoginRes saveMember(String providerName, KakaoProfile kakaoProfile) {
        String kakaoId = kakaoProfile.getId() + "_" + providerName; //카카오에서 받아온 아이디를 kakaoId로.
        String username = kakaoProfile.getProperties().getNickname();  //카카오에서 받아온 nickname을 username으로.
        String imageUrl = kakaoProfile.getProperties().getProfile_image(); //카카오에서 받아온 Profile_image를 imageUrl로.


        //멤버가 db에 있을 경우 멤버 아이디를 얻고, 없을 경우 db에 멤버값을 저장한다.
        Long memberId = null;
        Boolean isFirst = null;
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        if(member.isPresent()){ //db에 카카오아이디가 있으면 카카오아이디를 통해, memberId를 얻는다.
            memberId = member.get().getId();
            isFirst = false;
        } else{ //db에 카카오아이디가 없으면 member을 만들어준다.
            Member newMember = memberRepository.save(Member.createMember(kakaoId, username, imageUrl));
            memberId = newMember.getId();
            isFirst = true;
        }
        PrincipalDetails principalDetails = new PrincipalDetails(memberId, username); //principalDetails을 생성해줌
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new MemberDto.LoginRes(new MemberDto.MemberRes(memberId, username, imageUrl), isFirst);
    }

}
