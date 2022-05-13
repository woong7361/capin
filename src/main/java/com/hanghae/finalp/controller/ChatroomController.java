package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.ChatroomDto;
import com.hanghae.finalp.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;

    /**
     * dm방 만들기 API -> message를 받았을때 채팅방을 만들면 redis, db 둘다 네트워크를 타서 실행되지 않는 메시지가 생긴다.
     */
    @PostMapping("/api/chat/dm")
    public ChatroomDto.CreateRes createDmChatRoom(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ChatroomDto.CreateReq createReq) {
        Long dmRoomId = chatroomService.createDmChatroom(principalDetails.getMemberId(), createReq.getSideMemberId());
        return new ChatroomDto.CreateRes(dmRoomId);
    }


}
