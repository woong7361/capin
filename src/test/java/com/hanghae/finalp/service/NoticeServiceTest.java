package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.Notice;
import com.hanghae.finalp.entity.dto.NoticeDto;
import com.hanghae.finalp.repository.NoticeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    NoticeService noticeService;

    @Mock
    NoticeRepository noticeRepository;

    private Member member1;
    private Member member2;
    private Group group1;
    private Group group2;
    private Notice notice1;
    private Notice notice2;
    private Notice notice3;
    private Notice notice4;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");

        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 5, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");

        notice1 = Notice.createGroupApplyNotice(group1.getGroupTitle(), member1.getUsername(), member2);
        notice2 = Notice.createGroupApproveNotice(group1.getGroupTitle(), member1);
        notice3 = Notice.createGroupBanNotice(group1.getGroupTitle(), member1);
        notice4 = Notice.createGroupDenyNotice(group2.getGroupTitle(), member2);
    }

    @Nested
    class getNotice {
        @Test
        public void 성공() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<Notice>(List.of(notice1, notice2, notice3));
            PageRequest pageable = PageRequest.of(0, 3);
            given(noticeRepository.findByMemberId(anyLong(), any(PageRequest.class))).willReturn(slice);
            //when
            Slice<NoticeDto.MessageRes> notices = noticeService.getNotice(1L, pageable);
            //then
            assertThat(notices.getContent().size()).isEqualTo(3);
            assertThat(notices.getContent().stream()
                    .filter(m -> m.getIsRead().equals(true))
                    .collect(Collectors.toList()).size())
                    .isEqualTo(0);
            assertThat(notice1.getIsRead()).isTrue();
            assertThat(notice2.getIsRead()).isTrue();
            assertThat(notice3.getIsRead()).isTrue();
        }

        @Test
        public void 알림이_없을때() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<Notice>(List.of());
            PageRequest pageable = PageRequest.of(0, 3);
            given(noticeRepository.findByMemberId(anyLong(), any(PageRequest.class))).willReturn(slice);
            //when
            Slice<NoticeDto.MessageRes> notices = noticeService.getNotice(1L, pageable);
            //then
            assertThat(notices.getContent().size()).isEqualTo(0);
        }
    }

    @Nested
    class getNonReadCount {
        @Test
        public void 성공() throws Exception{
            //given
            given(noticeRepository.findNonReadCountById(anyLong()))
                    .willReturn(3L);
            //when //then
            noticeService.getNonReadCount(1L);
        }
    }

    @Nested
    class deleteNotice {
        @Test
        public void 성공() throws Exception{
            //given
            Member member = Member.createMappingMember(1L);
            Notice notice = Notice.createGroupBanNotice(group1.getGroupTitle(), member);
            given(noticeRepository.findById(anyLong()))
                    .willReturn(Optional.of(notice));
            //when //then
            noticeService.deleteNotice(1L, 2L);
        }

        @Test
        public void 자신의_알림이_아닌경우() throws Exception{
            //given
            Member member = Member.createMappingMember(1L);
            Notice notice = Notice.createGroupBanNotice(group1.getGroupTitle(), member);
            given(noticeRepository.findById(anyLong()))
                    .willReturn(Optional.of(notice));
            //when //then
            assertThatThrownBy(() -> noticeService.deleteNotice(3L, 2L))
                    .isInstanceOf(AuthorOwnerException.class);
        }
    }


}