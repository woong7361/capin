package com.hanghae.finalp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class NoticeTest {
    @Autowired
    private EntityManager em;

    public void clearPersistenceContext(){
        em.flush();
        em.clear();
    }


    @Test
    @Order(1)
    @DisplayName("알람 생성 테스트")
    public void noticeCreateTest() throws Exception {
        Member ownerMember = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Notice notice = Notice.createGroupApplyNotice("groupTitle", "targetUsername", ownerMember);

        em.persist(ownerMember);
        em.persist(notice);
        clearPersistenceContext();

        assertEquals("targetUsername님이 groupTitle그룹에 가입신청을 했습니다.", notice.getMessage());
        assertEquals(false, notice.getIsRead());
        assertEquals(ownerMember, notice.getMember());
    }

    @Test
    @Order(2)
    @DisplayName("알람 검색 테스트")
    public void noticeFindTest() throws Exception {
        Member ownerMember = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Notice notice = Notice.createGroupApplyNotice("groupTitle", "targetUsername", ownerMember);

        em.persist(ownerMember);
        em.persist(notice);
        clearPersistenceContext();

        Notice findNotice = em.find(Notice.class, notice.getId());

        assertEquals("targetUsername님이 groupTitle그룹에 가입신청을 했습니다.", findNotice.getMessage());
        assertEquals(false, findNotice.getIsRead());
        assertEquals("kakaoId", findNotice.getMember().getKakaoId());
    }

    @Test
    @Order(3)
    @DisplayName("알람 삭제 테스트")
    public void noticeDeleteTest() throws Exception {
        Member ownerMember = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Notice notice = Notice.createGroupApplyNotice("groupTitle", "targetUsername", ownerMember);

        em.persist(ownerMember);
        em.persist(notice);
        clearPersistenceContext();

        em.remove(em.find(Notice.class, notice.getId()));
        clearPersistenceContext();

        Notice findNotice = em.find(Notice.class, notice.getId());

        assertNull(findNotice);
    }
}