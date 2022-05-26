package com.hanghae.finalp.repository;

import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        em.flush();
        em.clear();
    }
    
    @Test
    public void findByKakaoId_쿼리_테스트() throws Exception{
        //given //when
        Member findMember1 = memberRepository.findByKakaoId(member1.getKakaoId())
                .orElseThrow(MemberNotExistException::new);
        Member findMember2 = memberRepository.findByKakaoId(member2.getKakaoId())
                .orElseThrow(MemberNotExistException::new);
        Member findMember3 = memberRepository.findByKakaoId(member3.getKakaoId())
                .orElseThrow(MemberNotExistException::new);

        //then
        assertThat(findMember1.getUsername()).isEqualTo(member1.getUsername());
        assertThat(findMember2.getImageUrl()).isEqualTo(member2.getImageUrl());
        assertThat(findMember3.getId()).isEqualTo(member3.getId());
    }

}