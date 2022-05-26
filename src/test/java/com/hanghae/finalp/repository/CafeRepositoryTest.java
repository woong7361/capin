package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class CafeRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired CafeRepository cafeRepository;
    @Autowired EntityManager em;

    private Member member;
    private Group group;
    private Cafe cafe;

    @BeforeEach
    public void init() {
        member = Member.createMember("kakaoId", "userA", "image");
        memberRepository.save(member);
        group = Group.createGroup("title", "desc", 5, "강남",
                "image", member, 999L, "2022.01.07", "2022.02.09");
        groupRepository.save(group);
        cafe = Cafe.createCafe("loc", "1.12", "2.22", "address", group);
        cafeRepository.save(cafe);
        em.flush();
        em.clear();
    }

    @Test
    public void deleteByGroupId_정상_삭제() throws Exception{
        //given //when
        cafeRepository.deleteByGroupId(group.getId());
        Optional<Cafe> findCafe = cafeRepository.findById(cafe.getId());
        //then
        Assertions.assertThat(findCafe.isEmpty()).isTrue();
    }

    @Test
    public void deleteByGroupId_실패() throws Exception{
        //given //when
        cafeRepository.deleteByGroupId(member.getId());
        Optional<Cafe> findCafe = cafeRepository.findById(cafe.getId());
        //then
        Assertions.assertThat(findCafe.isEmpty()).isFalse();
    }
}