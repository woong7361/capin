package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/api/groups/my")
    public Slice<GroupDto.SimpleRes> myGroupList(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable) {
        return groupService.getMyGroupList(principalDetails.getPrincipal().getMemberId(), pageable);
    }
}
