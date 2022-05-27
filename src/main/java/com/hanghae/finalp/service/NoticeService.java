package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.entity.Notice;
import com.hanghae.finalp.entity.dto.NoticeDto;
import com.hanghae.finalp.repository.NoticeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;


    /**
     * 알림 가져오기
     */
    @Transactional
    public Slice<NoticeDto.MessageRes> getNotice(Long memberId, Pageable pageable) {
        Slice<Notice> notices = noticeRepository.findByMemberId(memberId, pageable);
        Slice<NoticeDto.MessageRes> noticeSlice = notices.map(NoticeDto.MessageRes::new);
        for (Notice notice : notices) {
            notice.readNotice();
        }
        return noticeSlice;
    }

    /**
     * 읽지않은 알림 개수 가져오기
     */
    public NoticeDto.NonReadCountRes getNonReadCount(Long memberId) {
        return new NoticeDto.NonReadCountRes(noticeRepository.findNonReadCountById(memberId));
    }

    /**
     * 알림 지우기
     */
    public void deleteNotice(Long memberId, Long noticeId) {
        noticeRepository.findById(noticeId).filter(notice -> memberId.equals(notice.getMember().getId()))
                .orElseThrow(AuthorOwnerException::new);
        noticeRepository.deleteById(noticeId);
    }

}
