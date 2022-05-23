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


    @Transactional
    public Slice<NoticeDto.Res> getNotice(Long memberId, Pageable pageable) {
        Slice<Notice> notices = noticeRepository.findByMemberId(memberId, pageable);
        Slice<NoticeDto.Res> noticeSlice = notices.map(NoticeDto.Res::new);
        for (Notice notice : notices) {
            notice.read();
        }
        return noticeSlice;
    }

    public NoticeDto.NonReadCountRes getNonReadCount(Long memberId) {
        return new NoticeDto.NonReadCountRes(noticeRepository.findNonReadCountById(memberId));
    }

    public void deleteNotice(Long memberId, Long noticeId) {
        //자신의 알림인지 확인
        noticeRepository.findById(noticeId).filter(notice -> memberId.equals(notice.getMember().getId()))
                .orElseThrow(AuthorOwnerException::new);
        noticeRepository.deleteById(noticeId);
    }
}
