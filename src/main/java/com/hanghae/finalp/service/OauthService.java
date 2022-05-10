package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.dto.ResultMsg;
import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.config.security.kakao.KakaoProfile;
import com.hanghae.finalp.config.security.kakao.OAuthToken;
import com.hanghae.finalp.config.security.jwt.JwtTokenUtils;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
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
public class OauthService {

    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<ResultMsg> login(String providerName, String code) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        OAuthToken tokenResponse = getToken(code, provider);
        KakaoProfile kakaoProfile = getUserProfile(providerName, tokenResponse, provider);

        PrincipalDetails principalDetails = saveMember(providerName, kakaoProfile);

        String token = JwtTokenUtils.createToken(principalDetails);

        ResponseEntity<ResultMsg> response = makeTokenResponse(token);

        return response;
    }




    //==============================================================================================//

    private OAuthToken getToken(String code, ClientRegistration provider) {
        OAuthToken oAuthToken = WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenReqeust(code, provider))
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .block();

        log.info("kakao token response = {}", oAuthToken.toString());
        return oAuthToken;
    }

    private MultiValueMap<String, String> tokenReqeust(String code, ClientRegistration provider) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
//        formData.add("client_secret", provider.getClientSecret());
        formData.add("client_id", provider.getClientId());
        return formData;
    }

    private KakaoProfile getUserProfile(String providerName, OAuthToken tokenResponse, ClientRegistration provider) {
        KakaoProfile kakaoProfile = WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccess_token()))
                .retrieve()
                .bodyToMono(KakaoProfile.class)
                .block();

        log.info("kakao profile response = {}", kakaoProfile.toString());
        return kakaoProfile;
    }

    private PrincipalDetails saveMember(String providerName, KakaoProfile kakaoProfile) {
        String kakaoId = kakaoProfile.getId() + "_" + providerName;
        String username = kakaoProfile.getProperties().getNickname();
        String imageUrl = kakaoProfile.getProperties().getProfile_image();

        Long memberId = null;
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        if(member.isPresent()){
            memberId = member.get().getId();
        } else{
            Member newMember = memberRepository.save(Member.createMember(kakaoId, username, imageUrl));
            memberId = newMember.getId();
        }
        PrincipalDetails principalDetails = new PrincipalDetails(memberId, username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return principalDetails;
    }

    private ResponseEntity<ResultMsg> makeTokenResponse(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtTokenUtils.TOKEN_HEADER_NAME, token);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResultMsg("success", "추후 삭제 예정 - 편의상"  + token));
    }
}
