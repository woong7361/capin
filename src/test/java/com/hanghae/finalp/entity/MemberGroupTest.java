package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class MemberGroupTest {
    @Autowired
    private EntityManager em;

    public void clearPersistenceContext(){
        em.flush();
        em.clear();
    }

    @Test
    @Order(1)
    @DisplayName("멤버그룹 생성 테스트")
    public void memberGroupCreateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.JOIN, member, group, 1L);

        em.persist(member);
        em.persist(group);
        em.persist(memberGroup);
        clearPersistenceContext();

        assertEquals(Authority.JOIN, memberGroup.getAuthority());
        assertEquals(member, memberGroup.getMember());
        assertEquals(group, memberGroup.getGroup());
        assertEquals(1L, memberGroup.getChatroomId());
    }

    @Test
    @Order(2)
    @DisplayName("멤버그룹 검색 테스트")
    public void memberGroupFindTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.JOIN, member, group, 1L);

        em.persist(member);
        em.persist(group);
        em.persist(memberGroup);
        clearPersistenceContext();

        MemberGroup findMemberGrouop = em.find(MemberGroup.class, memberGroup.getId());

        assertEquals(Authority.JOIN, findMemberGrouop.getAuthority());
        assertEquals("kakaoId", findMemberGrouop.getMember().getKakaoId());
        assertEquals("groupTitle", findMemberGrouop.getGroup().getGroupTitle());
        assertEquals(1L, memberGroup.getChatroomId());
    }

    @Test
    @Order(3)
    @DisplayName("멤버그룹 삭제 테스트")
    public void memberGroupDeleteTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.JOIN, member, group, 1L);

        em.persist(member);
        em.persist(group);
        em.persist(memberGroup);
        clearPersistenceContext();

        em.remove(em.find(MemberGroup.class, memberGroup.getId()));
        clearPersistenceContext();

        MemberGroup findMemberGroup = em.find(MemberGroup.class, memberGroup.getId());

        assertNull(findMemberGroup);
    }
}