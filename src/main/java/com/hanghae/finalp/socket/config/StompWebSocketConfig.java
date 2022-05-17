package com.hanghae.finalp.socket.config;

import com.hanghae.finalp.service.ChatService;
import com.hanghae.finalp.socket.StompHandler;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {


//    private final StompHandler stompHandler;
    private final JwtTokenUtils jwtTokenUtils;
    private final ChatService chatService;
    private final RedisUtils redisUtils;

    // sub, pub prefix 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry
//                .addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }


    // stomp interceptor설정 -> webSocket의 message뿐만 아니라 connect, sub, disconnect 감지 가능
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler());
    }

    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE + 99)
    public ChannelInterceptor stompHandler() {
        return new StompHandler(jwtTokenUtils, chatService, redisUtils);
    }
}


