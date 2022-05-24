package com.hanghae.finalp.controller;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.*;
import com.hanghae.finalp.entity.dto.scraping.KakaoApiDto;
import com.hanghae.finalp.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;


    //카페 선택 -> 생성과 같음
    @PostMapping("/api/groups/{groupId}/cafe")
    public ResultMsg cafeSelect(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @PathVariable("groupId") Long groupId,
                                @Valid @RequestBody CafeDto.Reqeust request) { //무슨값 받을지 확실x 수정가능성 있음
        cafeService.selectCafe(principalDetails.getMemberId(), request, groupId);
        return new ResultMsg("success");
    }


    //카페 삭제
    @PostMapping("/api/groups/{groupId}/cafe/delete")
    public ResultMsg cafeDelete(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           @PathVariable("groupId") Long groupId) {
        cafeService.deleteCafe(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }



    //스터디 카페 추천
    @GetMapping("/api/groups/{groupId}/cafe-recommendation")
    public CafeDto.RecoRes locationRecommend(@PathVariable("groupId") Long groupId) {
        return cafeService.getRecoCafe(groupId);
    }

}


