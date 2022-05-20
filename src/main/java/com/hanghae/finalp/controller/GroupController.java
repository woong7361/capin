package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
import com.hanghae.finalp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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
            @Valid GroupDto.CreateReq createReq,
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
            @Valid GroupDto.CreateReq createReq,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @PathVariable("groupId") Long groupId
    ) {
        groupService.patchGroup(principalDetails.getMemberId(), groupId, createReq, multipartFile);
        return new ResultMsg("success");
    }


    //--------------------------------------------------------------------------------------


    //그룹 리스트 페이징, 검색
    @GetMapping("/api/groups/list")
    public Page<Group> GroupList(@PageableDefault(size = 20, sort = "groupId", direction = Sort.Direction.DESC) Pageable pageable,
                                 @PathVariable("groupId") Long groupId, String searchKeyword) {

        Page<Group> list = null;

        if(searchKeyword == null) {
            list = groupService.getGroupList(groupId, pageable);
        } else {
            list = groupService.groupSearch(searchKeyword, pageable);
        }

//        int nowPage = list.getPageable().getPageNumber() + 1; //페이지는 0부터 시작하므로 +1
//        int startPage = Math.max(0,nowPage - 4);
//        int endPage = Math.min(list.getTotalPages(), nowPage + 5);

        return list;
    }

    //특정 그룹
    @GetMapping("/api/groups/{groupId}")
    public Slice<Group> groupView(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @PathVariable("groupId") Long groupId
    ){
        return groupService.groupView(groupId);
    }



    //---------------------------------------------------------------------------



}
