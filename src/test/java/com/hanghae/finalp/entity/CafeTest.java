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

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class CafeTest {
    @Autowired
    private EntityManager em;

    public void clearPersistenceContext(){
        em.flush();
        em.clear();
    }

    @Test
    @Order(1)
    @DisplayName("카페 생성 테스트")
    public void cafeCreateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        Cafe cafe = Cafe.createCafe("locationName", "127.0545", "38.3832", "address", group);

        em.persist(member);
        em.persist(group);
        em.persist(cafe);
        clearPersistenceContext();

        assertEquals("locationName", cafe.getLocationName());
        assertEquals(group, cafe.getGroup());
    }

    @Test
    @Order(2)
    @DisplayName("카페 검색 테스트")
    public void cafeFindTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        Cafe cafe = Cafe.createCafe("locationName", "127.0545", "38.3832", "address", group);

        em.persist(member);
        em.persist(group);
        em.persist(cafe);
        clearPersistenceContext();

        Cafe findCafe = em.find(Cafe.class, cafe.getId());

        assertEquals("locationName", findCafe.getLocationName());
        assertEquals("groupTitle", findCafe.getGroup().getGroupTitle());
    }

    @Test
    @Order(3)
    @DisplayName("카페 삭제 테스트")
    public void cafeDeleteTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        Cafe cafe = Cafe.createCafe("locationName", "127.0545", "38.3832", "address", group);

        em.persist(member);
        em.persist(group);
        em.persist(cafe);
        clearPersistenceContext();

        em.remove(em.find(Cafe.class, cafe.getId()));
        clearPersistenceContext();

        Cafe findCafe = em.find(Cafe.class, cafe.getId());

        assertNull(findCafe);
    }

}