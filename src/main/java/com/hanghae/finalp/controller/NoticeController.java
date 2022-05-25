package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.NoticeDto;
import com.hanghae.finalp.entity.dto.other.ResultMsg;
import com.hanghae.finalp.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 알림 리스트 가져오기
     */
    @GetMapping("/api/notices")
    public Slice<NoticeDto.MessageRes> getNotice(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return noticeService.getNotice(principalDetails.getMemberId(), pageable);
    }

    /**
     * 읽지않은 알림 숫자 보기
     */
    @GetMapping("/api/notices/non-read")
    public NoticeDto.NonReadCountRes getNonReadCount(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return noticeService.getNonReadCount(principalDetails.getMemberId());
    }

    /**
     * 알림 삭제
     */
    @PostMapping("/api/notices/{noticeId}/delete")
    public ResultMsg deleteNotice(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  @PathVariable("noticeId") Long noticeId) {
        noticeService.deleteNotice(principalDetails.getMemberId(), noticeId);
        return new ResultMsg("success");
    }

}
