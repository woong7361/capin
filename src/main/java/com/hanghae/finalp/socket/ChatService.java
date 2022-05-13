package com.hanghae.finalp.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatService {
    // Redis CacheKeys

    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Chatroom> hashOpsChatRoom;
    //    roomId, memberId
    @Resource(name = "redisTemplate")
    private SetOperations<String, String> roomMemberOps;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, MemberDto.RedisPrincipal> principalOps;

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    private final ChatRoomRepository chatRoomRepository;

//    // 모든 채팅방 조회
//    public List<Chatroom> findAllRoom() {
//        return hashOpsChatRoom.values(CHAT_ROOMS);
//    }
//
//    // 특정 채팅방 조회
//    public Chatroom findRoomById(String id) {
//        return hashOpsChatRoom.get(CHAT_ROOMS, id);
//    }
//
//    // 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
//    public Chatroom createChatRoom(Long chatroomId) {
////        Chatroom chatRoom = Chatroom.create(name);
//        Chatroom chatroom = chatRoomRepository.findById(chatroomId).get();
//        hashOpsChatRoom.put(CHAT_ROOMS, chatroom.getId().toString(), chatroom);
//        return chatroom;
//    }

    public void addRoomMember(String roomId, String memberId) {
        roomMemberOps.add(roomId, memberId);
    }
    public void removeRoomMember(String roomId, String memberId) {
        roomMemberOps.remove(roomId, memberId);
    }
    public Set<String> getRoomMembers(String roomId) {
        return roomMemberOps.members(roomId);
    }


    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, Long memberId, String username, Long roomId) {
        principalOps.put(ENTER_INFO, sessionId, new MemberDto.RedisPrincipal(memberId, username, roomId));
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public MemberDto.RedisPrincipal getUserEnterInfo(String sessionId) {
        return principalOps.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        principalOps.delete(ENTER_INFO, sessionId);
    }

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    public void sendChatMessage(MessageDto.Send message) {
        if (message.getMessageType().equals(MessageType.ENTER)) {
            message.setContent(message.getSenderName() + "님이 입장하였습니다");
            message.setSenderName("[알림]");
        } else if (message.getMessageType().equals(MessageType.QUIT)) {
            message.setContent(message.getSenderName() + "님이 입장하였습니다");
            message.setSenderName("[알림]");
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}

