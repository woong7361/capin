package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.dto.GroupDto;
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
class GroupTest {

    @Autowired private EntityManager em;

    public void clearPersistenceContext(){
        em.flush();
        em.clear();
    }

    @Test
    @Order(1)
    @DisplayName("그룹 생성 테스트")
    public void groupCreateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");

        em.persist(member);
        em.persist(group);
        clearPersistenceContext();

        assertEquals("groupTitle", group.getGroupTitle());
        assertEquals(member, group.getMemberGroups().get(0).getMember());
    }


    @Test
    @Order(2)
    @DisplayName("그룹 검색 테스트")
    public void groupFindTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");

        em.persist(member);
        em.persist(group);
        clearPersistenceContext();

        Group findGroup = em.find(Group.class, group.getId());

        assertEquals("groupTitle", findGroup.getGroupTitle());
        assertEquals("kakaoId", findGroup.getMemberGroups().get(0).getMember().getKakaoId());
    }

    @Test
    @Order(3)
    @DisplayName("그룹 수정 테스트")
    public void groupUpdateTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");

        em.persist(member);
        em.persist(group);
        clearPersistenceContext();

        GroupDto.CreateReq createReq = new GroupDto.CreateReq("newTitle","newDesc",20,"newAddress","2022.02.02","2022.05.30");
        group.patch( createReq, "https://d2yjfe20.cloudfront.net/newImg.png");

        assertEquals("newTitle", group.getGroupTitle());
        assertEquals("https://d2yjfe20.cloudfront.net/newImg.png", group.getImageUrl());
    }

    @Test
    @Order(4)
    @DisplayName("그룹 삭제 테스트")
    public void groupDeleteTest() throws Exception {
        Member member = Member.createMember("kakaoId", "홍길동", "https://d2yjfe20.cloudfront.net/img.png");
        Group group = Group.createGroup("groupTitle", "desc", 10, "address",
                "https://d2yjfe20.cloudfront.net/groupImg.png", member, 1L, "2022.02.02", "2022.05.05");
        Cafe cafe = Cafe.createCafe("locationName", "127.0000", "37.0000", "addredss", group);

        em.persist(member);
        em.persist(group);
        clearPersistenceContext();

        em.remove(em.find(Group.class, group.getId()));
        clearPersistenceContext();

        Group findGroup = em.find(Group.class, group.getId());

        assertNull(findGroup);
    }
}