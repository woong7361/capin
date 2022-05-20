package com.hanghae.finalp.controller;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
import com.hanghae.finalp.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;


    //카페 선택 -> 생성과 같음
    @PostMapping("/api/groups/{groupId}/cafe")
    public ResultMsg cafeSelect(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @PathVariable("groupId") Long groupId,
                                @Valid CafeDto.Reqeust cafeReq) { //무슨값 받을지 확실x 수정가능성 있음
        cafeService.selectCafe(principalDetails.getMemberId(), cafeReq, groupId);
        return new ResultMsg("success");
    }


    //카페 삭제
    @PostMapping("/api/groups/{groupId}/cafe/delete")
    public ResultMsg cafeDelete(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           @PathVariable("groupId") Long groupId) {
        cafeService.deleteCafe(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }

}
