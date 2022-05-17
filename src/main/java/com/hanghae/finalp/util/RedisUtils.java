package com.hanghae.finalp.util;

import com.hanghae.finalp.entity.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장


//    @Resource(name = "redisTemplate")
//    private SetOperations<String, String> roomMemberOps;
//    @Resource(name = "redisTemplate")
//    private HashOperations<String, String, MemberDto.RedisPrincipal> principalOps;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private SetOperations<String, String> roomMemberOps;
    private HashOperations<String, String, MemberDto.RedisPrincipal> principalOps;

    @PostConstruct
    public void init() {
        this.roomMemberOps = stringRedisTemplate.opsForSet();
        this.principalOps = redisTemplate.opsForHash();
    }


    public void addRoomMember(String roomId, Long memberId, String username) {
        roomMemberOps.add(roomId, (memberId.toString() + "_" + username));
    }

    public void removeRoomMember(String roomId, Long memberId, String username) {
        roomMemberOps.remove(roomId, memberId + "_" + username);
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



    public String getRefreshTokenData(String key) {
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }
    public void setRefreshTokenDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration / 1000);
        valueOperations.set(key, value, expireDuration);
    }
    public void deleteRefreshTokenData(String key) {
        stringRedisTemplate.delete(key);
    }

}
