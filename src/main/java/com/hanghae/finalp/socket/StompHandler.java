package com.hanghae.finalp.socket;


import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

//    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
//            String jwtToken = accessor.getFirstNativeHeader("token");
//            log.info("CONNECT {}", jwtToken);
//            // Header의 jwt token 검증
//            jwtTokenProvider.validateToken(jwtToken);
        }
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = getRoomIdFromheader(message.getHeaders());
            String sessionId = getSessionIdFromHeader(message.getHeaders());
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            chatService.setUserEnterInfo(sessionId, roomId);
            chatService.addRoomMember(roomId, sessionId);

            sendMessage(roomId, MessageType.ENTER);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = getSessionIdFromHeader(message.getHeaders());
            String roomId = chatService.getUserEnterRoomId(sessionId);

            // socket disconnect시 disconnect로 2번 들어옴 이유가 뭘까요?
            if (roomId == null) return;

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatService.removeUserEnterInfo(sessionId);
            chatService.removeRoomMember(roomId, sessionId);

            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            sendMessage(roomId, MessageType.QUIT);
        }
    }


    //=========================================sub logic=================================================//


    private void sendMessage(String roomId, MessageType messageType) {
        // 클라이언트 입장, 퇴장 메시지를 채팅방에 발송한다.(redis publish) - token에서 username, userId 추출
//            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//            chatRoomRepository.findById(Long.valueOf(roomId)).map(Chatroom::getRoomType).orElseThrow(RuntimeException::new);
        MessageDto.Send sendMessage = MessageDto.Send.builder().
                chatroomId(roomId).messageType(messageType).senderId(1L).senderName("TOKEN추출").build();
        chatService.sendChatMessage(sendMessage);

        log.info("Type: {}, roomId: {}",messageType, roomId);
        log.info("count: {} , members: {}", chatService.getRoomMembers(roomId).size(), chatService.getRoomMembers(roomId));
    }

    private String getSessionIdFromHeader(MessageHeaders headers) {
        return (String) headers.get("simpSessionId");
    }


    private String getRoomIdFromheader(MessageHeaders headers) {
        return chatService.getRoomId(Optional.ofNullable((String) headers.get("simpDestination"))
                .orElse("InvalidRoomId"));
    }
}