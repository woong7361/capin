package com.hanghae.finalp;

import com.hanghae.finalp.entity.Board;
import com.hanghae.finalp.entity.Color;
import com.hanghae.finalp.entity.DayNight;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class FinalPApplicationTests {

    @Autowired private EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Rollback(value = false)
    void contextLoads() {
        Member member = Member.createMember("username1", "kakaoId1");
        em.persist(member);

        Member findMember = memberRepository.findById(member.getId()).get();


        Color color = Color.createColor(1, 2, 3, DayNight.DAY, member);

        Board board = Board.createBoard("title1", "content", findMember, color, new ArrayList<>(Arrays.asList("1", "2")));

        em.persist(color);
        em.persist(board);

    }

}
