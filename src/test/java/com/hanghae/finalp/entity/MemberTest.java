package com.hanghae.finalp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
class MemberTest {
    @Autowired private EntityManager em;

    public void clearPersistenceContext(){
        em.flush();
        em.clear();
    }


    @Test
    @Order(1)
    @DisplayName("멤버 생성 테스트")
    public void memberCreateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearPersistenceContext();

        assertEquals("kakaoId", member.getKakaoId());
        assertEquals("홍길동", member.getUsername());
        assertEquals("https://d2yjfe20.cloudfront.net/img.png", member.getImageUrl());
    }


    @Test
    @Order(2)
    @DisplayName("멤버 검색 테스트")
    public void memberFindTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearPersistenceContext();

        Member findMember = em.find(Member.class, member.getId());

        assertEquals("kakaoId", findMember.getKakaoId());
        assertEquals("홍길동", findMember.getUsername());
        assertEquals("https://d2yjfe20.cloudfront.net/img.png", findMember.getImageUrl());
    }

    @Test
    @Order(3)
    @DisplayName("멤버 수정 테스트")
    public void memberUpdateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearPersistenceContext();

        member.patchMember( "김철수", "https://d2yjfe20.cloudfront.net/ggg.png");

        assertEquals("김철수", member.getUsername());
        assertEquals("https://d2yjfe20.cloudfront.net/ggg.png", member.getImageUrl());
    }


    @Test
    @Order(4)
    @DisplayName("멤버 삭제 테스트")
    public void memberDeleteTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearPersistenceContext();

        em.remove(em.find(Member.class, member.getId()));
        clearPersistenceContext();

        Member findMember = em.find(Member.class, member.getId());

        assertNull(findMember);
    }


}