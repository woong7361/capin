package com.hanghae.finalp.repository;

import com.hanghae.finalp.config.exception.customexception.entity.EntityNotExistException;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class NoticeRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired NoticeRepository noticeRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired EntityManager em;
    
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
        memberRepository.save(member1);
        memberRepository.save(member2);
        
        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 5, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");
        groupRepository.save(group1);
        groupRepository.save(group2);

        notice1 = Notice.createGroupApplyNotice(group1.getGroupTitle(), member1.getUsername(), member2);
        notice2 = Notice.createGroupApproveNotice(group1.getGroupTitle(), member1);
        notice3 = Notice.createGroupBanNotice(group1.getGroupTitle(), member1);
        notice4 = Notice.createGroupDenyNotice(group2.getGroupTitle(), member2);
        noticeRepository.save(notice1);
        noticeRepository.save(notice2);
        noticeRepository.save(notice3);
        noticeRepository.save(notice4);

        em.flush();
        em.clear();
    }

    
    @Test
    public void findByMemberId_쿼리_테스트() throws Exception{
        //given //when
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Notice> member1Notices = noticeRepository.findByMemberId(member1.getId(), pageable);
        Slice<Notice> member2Notices = noticeRepository.findByMemberId(member2.getId(), pageable);

        //then
        assertThat(member1Notices.getContent().size()).isEqualTo(2);
        assertThat(member2Notices.getContent().size()).isEqualTo(2);
    }

    @Test
    public void findNonReadCountById_쿼리_테스트() throws Exception{
        //given //when
        long beforeReadCount = noticeRepository.findNonReadCountById(member1.getId());

        Notice findNotice2 = noticeRepository.findById(notice2.getId()).orElseThrow(EntityNotExistException::new);
        findNotice2.readNotice();
        em.flush();
        em.clear();

        long afterReadCount = noticeRepository.findNonReadCountById(member1.getId());

        //then
        assertThat(beforeReadCount).isEqualTo(2);
        assertThat(afterReadCount).isEqualTo(1);
    }
}