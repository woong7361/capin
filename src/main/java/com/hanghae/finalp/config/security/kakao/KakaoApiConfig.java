package com.hanghae.finalp.config.security.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@RequiredArgsConstructor
@Configuration
public class KakaoApiConfig {

    @Value("${spring.security.oauth2.client.registration.Kakao.client-id}")
    private String kakao_apikey;

    @Bean
    public WebClient kakaoWebClient(){
        return WebClient.builder().baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + kakao_apikey).build();
    }
}
