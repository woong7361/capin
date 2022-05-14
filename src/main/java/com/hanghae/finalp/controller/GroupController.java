package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
import com.hanghae.finalp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 내 그룹리스트 받아오기
     */
    @GetMapping("/api/groups/my")
    public Slice<GroupDto.SimpleRes> myGroupList(@AuthenticationPrincipal PrincipalDetails principalDetails, Pageable pageable) {
        return groupService.getMyGroupList(principalDetails.getPrincipal().getMemberId(), pageable);
    }

    /**
     * 스터디 그룹 생성
     */
    @PostMapping("/api/groups")
    public GroupDto.SimpleRes createGroup(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            GroupDto.CreateReq createReq,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile
    ) {
        return groupService.createGroup(principalDetails.getMemberId(), createReq, multipartFile);
    }

    /**
     * 스터디 그룹 삭제
     */
    @PostMapping("/api/groups/{groupId}/delete")
    public ResultMsg deleteGroup(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ) {
        groupService.deleteGroup(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }

    /**
     * 스터디 그룹 수정
     */
    @PostMapping("/api/groups/{groupId}/patch")
    public ResultMsg patchReq(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            GroupDto.CreateReq createReq,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @PathVariable("groupId") Long groupId
    ) {
        groupService.patchGroup(principalDetails.getMemberId(), groupId, createReq, multipartFile);
        return new ResultMsg("success");
    }


    //--------------------------------------------------------------------------------------

    //그룹 참가 신청
    @PostMapping("/api/groups/{groupId}/apply")
    public ResultMsg GroupApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId
    ){
        Long memberId = principalDetails.getMemberId();
        groupService.applyGroup(memberId, groupId);
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
        groupService.approveGroup(myMemberId, groupId, memberId);
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
        groupService.denyGroup(myMemberId, groupId, memberId);
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
        groupService.banGroup(myMemberId, groupId, memberId);
        return new ResultMsg("success");
    }

    //---------------------------------------------------------------------------

    //그룹 내 개인의 세부주소 작성
    @PostMapping("/api/groups/{groupId}/location")
    public ResultMsg locationSet(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable("groupId") Long groupId,
            MemberGroupDto.Request request
    ){
        Long memberId = principalDetails.getMemberId();
        groupService.setlocation(memberId, groupId, request);
        return new ResultMsg("success");
    }

    //스터디 카페 추천
    @GetMapping("/api/groups/{groupId}/cafe-recommendation")
    public MemberGroupDto.Response locationRecommend(@PathVariable("groupId") Long groupId){
        return groupService.recommendLocation(groupId);
    }


}
