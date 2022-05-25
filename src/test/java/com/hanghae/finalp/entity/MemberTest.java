package com.hanghae.finalp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@AutoConfigureTestDatabase(connection = H2)
@DataJpaTest //@DataJpaTest에는 기본적으로 @Transactional이 설정되어있다
class MemberTest {
    @Autowired private EntityManager em;

    public void clearContext(){
        em.flush();
        em.clear();
    }

    @Test
    @Order(1)
    @DisplayName("멤버 생성 테스트")
    public void memberCreateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearContext();

        assertEquals(member.getKakaoId(), "kakaoId");
        assertEquals(member.getUsername(), "홍길동");
        assertEquals(member.getImageUrl(), "https://d2yjfe20.cloudfront.net/img.png");
    }


    @Test
    @Order(2)
    @DisplayName("멤버 검색 테스트")
    public void memberFindTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearContext();

        Member findMember = em.find(Member.class, member.getId());

        assertEquals(findMember.getKakaoId(), "kakaoId");
        assertEquals(findMember.getUsername(), "홍길동");
        assertEquals(findMember.getImageUrl(), "https://d2yjfe20.cloudfront.net/img.png");
    }

    @Test
    @Order(3)
    @DisplayName("멤버 수정 테스트")
    public void memberUpdateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearContext();

        member.patchMember( "김철수", "https://d2yjfe20.cloudfront.net/ggg.png");

        assertEquals(member.getUsername(), "김철수");
        assertEquals(member.getImageUrl(), "https://d2yjfe20.cloudfront.net/ggg.png");
    }


    @Test
    @Order(4)
    @DisplayName("멤버 삭제 테스트")
    public void memberDeleteTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        em.persist(member);
        clearContext();

//        em.find(Member.class, member.getId());
//        em.remove(member);

//        em.remove(em.contains(member) ? member : em.merge(member));

        member = em.merge(member);
        em.remove(member);
//        assertEquals(member.getKakaoId(), null);
        System.out.println("======================================================================="+member);
        assertNull(member);
    }

}