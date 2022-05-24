package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
import com.hanghae.finalp.service.MemberGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberGroupController {

    private final MemberGroupService memberGroupService;


    //그룹 참가 신청
    @PostMapping("/api/groups/{groupId}/apply")
    public ResultMsg GroupApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.applyGroup(principalDetails.getMemberId(), principalDetails.getUsername(), groupId);
        return new ResultMsg("success");
    }


    //그룹 참가자 승인
    @PostMapping("/api/groups/{groupId}/approval/{memberId}")
    public ResultMsg GroupApproval(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long memberId //참가자 승인은 관리자가 하기때문에 memberId가 필요함
    ){
        Long myMemberId = principalDetails.getMemberId();
        memberGroupService.approveGroup(myMemberId, groupId, memberId);
        return new ResultMsg("success");
    }

    //그룹 참가자 거절
    @PostMapping("/api/groups/{groupId}/denial/{memberId}")
    public ResultMsg GroupDenial(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long memberId
    ){
        Long myMemberId = principalDetails.getMemberId();
        memberGroupService.denyGroup(myMemberId, groupId, memberId);
        return new ResultMsg("success");
    }


    //그룹 참가자 추방
    @PostMapping("/api/groups/{groupId}/ban/{memberId}")
    public ResultMsg GroupBan(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long memberId
    ){
        Long myMemberId = principalDetails.getMemberId();
        memberGroupService.banGroup(myMemberId, groupId, memberId);
        return new ResultMsg("success");
    }

    //그룹 참가신청 취소
    @PostMapping("/api/groups/{groupId}/cancel")
    public ResultMsg cancelApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.cancelApplyGroup(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }

    //그룹 참가신청 취소
    @PostMapping("/api/groups/{groupId}/exit")
    public ResultMsg exitGroup(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.exitGroup(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }

    //그룹 내 개인의 세부주소 작성
    @PostMapping("/api/groups/{groupId}/location")
    public ResultMsg locationSet(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody MemberGroupDto.Request request
    ){
        Long memberId = principalDetails.getMemberId();
        memberGroupService.setlocation(memberId, groupId, request);
        return new ResultMsg("success");
    }

}
