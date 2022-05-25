package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.dto.other.ResultMsg;
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

    /**
     * 그룹 참가 신청
     */
    @PostMapping("/api/groups/{groupId}/apply")
    public ResultMsg GroupApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.applyGroup(principalDetails.getMemberId(), principalDetails.getUsername(), groupId);
        return new ResultMsg("success");
    }


    /**
     * 그룹 참가자 승인
     */
    @PostMapping("/api/groups/{groupId}/approval/{memberId}")
    public ResultMsg GroupApproval(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long targetMemberId
    ){
        Long GroupOwnerMemberId = principalDetails.getMemberId();
        memberGroupService.approveGroup(GroupOwnerMemberId, groupId, targetMemberId);
        return new ResultMsg("success");
    }


    /**
     * 그룹 참가자 거절
     */
    @PostMapping("/api/groups/{groupId}/denial/{memberId}")
    public ResultMsg GroupDenial(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long targetMemberId
    ){
        Long GroupOwnerMemberId = principalDetails.getMemberId();
        memberGroupService.denyGroup(GroupOwnerMemberId, groupId, targetMemberId);
        return new ResultMsg("success");
    }


    /**
     * 그룹 참가자 추방
     */
    @PostMapping("/api/groups/{groupId}/ban/{memberId}")
    public ResultMsg GroupBan(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("memberId") Long targetMemberId
    ){
        Long GroupOwnerMemberId = principalDetails.getMemberId();
        memberGroupService.banGroup(GroupOwnerMemberId, groupId, targetMemberId);
        return new ResultMsg("success");
    }

    /**
     * 신청한 그룹 참가신청 취소
     */
    @PostMapping("/api/groups/{groupId}/cancel")
    public ResultMsg cancelApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.cancelApplyGroup(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }


    /**
     * 그룹 나가기
     */
    @PostMapping("/api/groups/{groupId}/exit")
    public ResultMsg exitGroup(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        memberGroupService.exitGroup(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }


    /**
     * 그룹내 개인의 세부 주소 작성
     */
    @PostMapping("/api/groups/{groupId}/location")
    public ResultMsg locationSet(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody MemberGroupDto.LocationReq locationReq
    ){
        Long memberId = principalDetails.getMemberId();
        memberGroupService.setlocation(memberId, groupId, locationReq);
        return new ResultMsg("success");
    }

}
