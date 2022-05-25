package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.ChatroomDto;
import com.hanghae.finalp.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;

    /**
     * dm방 만들기
     */
    @PostMapping("/api/chat/dm")
    public ChatroomDto.CreateRes createDmChatRoom(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ChatroomDto.CreateReq createReq) {
        Long dmRoomId = chatroomService.createDmChatroom(principalDetails.getMemberId(), createReq.getSideMemberId());
        return new ChatroomDto.CreateRes(dmRoomId);
    }

    /**
     * 채팅방 목록 가져오기
     */
    @GetMapping("/api/chat/list")
    public Slice<ChatroomDto.RoomRes> chatroomListRes(
            @AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable) {
        return chatroomService.getChatroomList(principalDetails.getMemberId(), pageable);
    }


}
