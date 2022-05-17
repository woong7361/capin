package com.hanghae.finalp.socket;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.service.ChatService;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Optional;
import java.util.Set;

import static com.hanghae.finalp.util.JwtTokenUtils.TOKEN_NAME_WITH_SPACE;

@Slf4j
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenUtils jwtTokenUtils;
    private final ChatService chatService;
    private final RedisUtils redisUtils;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            // Header의 jwt token 검증 -> interceptor error -> controller advice -> refresh token request 가능
            String accessToken = accessor.getFirstNativeHeader("Authorization")
                    .replace(TOKEN_NAME_WITH_SPACE, "");
            DecodedJWT decodedJWT = jwtTokenUtils.verifyToken(accessToken);

            Long memberId = jwtTokenUtils.getMemberIdFromClaim(decodedJWT);
            String username = jwtTokenUtils.getUsernameFromClaim(decodedJWT);

            String sessionId = getSessionIdFromHeader(message.getHeaders());
            redisUtils.setUserEnterInfo(sessionId, memberId, username, -1L);

//             simpUser헤더에 담기지가 않는다...
//            accessor.setUser(principal);
        }
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청

            String sessionId = getSessionIdFromHeader(message.getHeaders());
            String roomId = getRoomIdFromheader(message.getHeaders());

            MemberDto.RedisPrincipal principal = redisUtils.getUserEnterInfo(sessionId);
            redisUtils.setUserEnterInfo(sessionId, principal.getMemberId(), principal.getUsername(), Long.valueOf(roomId));
            redisUtils.addRoomMember(roomId, principal.getMemberId(), principal.getUsername());

            sendMessage(roomId, MessageType.ENTER, principal.getUsername(), principal.getMemberId());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료

            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = getSessionIdFromHeader(message.getHeaders());
            MemberDto.RedisPrincipal redisPrincipal = redisUtils.getUserEnterInfo(sessionId);
            // socket disconnect시 disconnect로 2번 들어옴 이유가 뭘까요?
            if (redisPrincipal == null) return;

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            redisUtils.removeUserEnterInfo(sessionId);
            redisUtils.removeRoomMember(redisPrincipal.getRoomId().toString(), redisPrincipal.getMemberId(), redisPrincipal.getUsername());


            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            sendMessage(redisPrincipal.getRoomId().toString(), MessageType.QUIT, redisPrincipal.getUsername(), redisPrincipal.getMemberId());
        }
    }


    //=========================================sub logic=================================================//


    private void sendMessage(String roomId, MessageType messageType, String username, Long memberId) {
        MessageDto.Send sendMessage = new MessageDto.Send(roomId, memberId, username, messageType);
        Set<String> roomMembers = redisUtils.getRoomMembers(roomId);
        chatService.sendChatMessage(sendMessage, roomMembers);

        log.info("Type: {}, roomId: {}, username: {}",messageType, roomId, username);
        log.info("count: {} , members: {}", redisUtils.getRoomMembers(roomId).size(), redisUtils.getRoomMembers(roomId));
    }

    private String getSessionIdFromHeader(MessageHeaders headers) {
        return (String) headers.get("simpSessionId");
    }


    private String getRoomIdFromheader(MessageHeaders headers) {
        return chatService.getRoomId(Optional.ofNullable((String) headers.get("simpDestination"))
                .orElse("InvalidRoomId"));
    }
}