package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@DataJpaTest
@AutoConfigureTestDatabase(connection = H2)
class GroupRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired MemberGroupRepository memberGroupRepository;
    @Autowired EntityManager em;

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Group group1;
    private Group group2;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        member4 = Member.createMember("kakaoId4", "userD", "image4");
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 5, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");
        groupRepository.save(group1);
        groupRepository.save(group2);

        MemberGroup memberGroup3 = MemberGroup.createMemberGroup(Authority.JOIN, member3, group1, 999L);
        MemberGroup memberGroup4 = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
        memberGroupRepository.save(memberGroup3);
        memberGroupRepository.save(memberGroup4);
        em.flush();
        em.clear();
    }


    @Test
    public void findAllByGroupTitleContaining_쿼리_테스트() throws Exception{
        //given //when
        PageRequest pageable = PageRequest.of(0, 5);
        Slice<Group> titleFind = groupRepository.findAllByGroupTitleContaining("title", pageable);
        Slice<Group> itFind = groupRepository.findAllByGroupTitleContaining("it", pageable);
        Slice<Group> title2Find = groupRepository.findAllByGroupTitleContaining("title2", pageable);
        Slice<Group> xxxFind = groupRepository.findAllByGroupTitleContaining("xxx", pageable);

        //then
        assertThat(titleFind.getContent().size()).isEqualTo(2);
        assertThat(itFind.getContent().size()).isEqualTo(2);
        assertThat(title2Find.getContent().size()).isEqualTo(1);
        assertThat(xxxFind.getContent().size()).isEqualTo(0);
    }


    @Test
    public void findAllByRoughAddressIn_쿼리_테스트() throws Exception{
        //given //when
        PageRequest pageable = PageRequest.of(0, 5);
        Slice<Group> SBfind = groupRepository.findAllByRoughAddressIn(List.of("서초", "방배"), pageable);
        Slice<Group> Sfind = groupRepository.findAllByRoughAddressIn(List.of("서초"), pageable);
        Slice<Group> Bfind = groupRepository.findAllByRoughAddressIn(List.of("방배"), pageable);
        Slice<Group> Gfind = groupRepository.findAllByRoughAddressIn(List.of("구로"), pageable);
        Slice<Group> SBGfind = groupRepository.findAllByRoughAddressIn(List.of("서초", "방배", "구로"), pageable);
        //then

        assertThat(SBfind.getContent().size()).isEqualTo(2);
        assertThat(Sfind.getContent().size()).isEqualTo(1);
        assertThat(Bfind.getContent().size()).isEqualTo(1);
        assertThat(Gfind.getContent().size()).isEqualTo(0);
        assertThat(SBGfind.getContent().size()).isEqualTo(2);
    }

    @Test
    public void findAllByGroupTitleContainingAndRoughAddressIn_쿼리_테스트() throws Exception{
        //given //when
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<Group> titileFind = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn("itl", List.of("서초"), pageable);
        Slice<Group> title1Find = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn("title1", List.of("서초", "방배"), pageable);
        Slice<Group> blankFind = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn("  ", List.of("서초", "방배"), pageable);
        Slice<Group> xxxFind = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn("xxxx", List.of("서초"), pageable);
        Slice<Group> sadangFind = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn("title", List.of("사당"), pageable);
//        //then

        assertThat(titileFind.getContent().size()).isEqualTo(1);
        assertThat(title1Find.getContent().size()).isEqualTo(1);
        assertThat(blankFind.getContent().size()).isEqualTo(0);
        assertThat(xxxFind.getContent().size()).isEqualTo(0);
        assertThat(sadangFind.getContent().size()).isEqualTo(0);
    }


    @Test
    public void findMemberByGroupId_쿼리_테스트() throws Exception{
        //given //when
        Optional<Group> find1 = groupRepository.findMemberByGroupId(group1.getId());
        Optional<Group> find2 = groupRepository.findMemberByGroupId(group2.getId());
        //then

        assertThat(find1.get().getMemberGroups().size()).isEqualTo(3);
        assertThat(find2.get().getMemberGroups().size()).isEqualTo(1);
    }

}