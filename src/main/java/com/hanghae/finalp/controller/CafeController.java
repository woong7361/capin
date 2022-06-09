package com.hanghae.finalp.controller;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.*;
import com.hanghae.finalp.entity.dto.other.ResultMsg;
import com.hanghae.finalp.service.CafeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;


    /**
     /* 카페 선택
     */
    @Operation(summary = "카페 선택", description = "그룹에서 카페 선택")
    @PostMapping("/api/groups/{groupId}/cafe")
    public ResultMsg cafeSelect(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                @PathVariable("groupId") Long groupId,
                                @Valid @RequestBody CafeDto.CreateReq request) {
        cafeService.selectCafe(principalDetails.getMemberId(), request, groupId);
        return new ResultMsg("success");
    }


    /**
     * 카페 삭제
     */
    @Operation(summary = "카페 삭제", description = "그룹에서 카페 삭제")
    @PostMapping("/api/groups/{groupId}/cafe/delete")
    public ResultMsg cafeDelete(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           @PathVariable("groupId") Long groupId) {
        cafeService.deleteCafe(principalDetails.getMemberId(), groupId);
        return new ResultMsg("success");
    }


    /**
     * 스터디 카페 추천
     */
    @Operation(summary = "스터디 카페 추천", description = "그룹에서 스터디 카페 추천")
    @GetMapping("/api/groups/{groupId}/cafe-recommendation")
    public CafeDto.RecoRes locationRecommend(@PathVariable("groupId") Long groupId) {
        return cafeService.getRecoCafe(groupId);
    }


    /**
     * 스터디 카페 추천
     */
    @Operation(summary = "스터디 카페 추천 - 메인페이지", description = "그룹에서 스터디 카페 추천")
    @GetMapping("/api/cafe-reco")
    public CafeDto.RecoRes locationRecommend(@Param("locationX") Double locationX, @Param("locationY") Double locationY) {
        return cafeService.getRecoCafeForMain(locationX, locationY);
    }

}


