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
class MemberGroupRepositoryTest {

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
    private Group group3;

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
        group3 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);

        MemberGroup memberGroup3 = MemberGroup.createMemberGroup(Authority.JOIN, member3, group1, 999L);
        MemberGroup memberGroup4 = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
        memberGroupRepository.save(memberGroup3);
        memberGroupRepository.save(memberGroup4);
        em.flush();
        em.clear();
    }


    @Test
    public void findMyGroupByMemberId_쿼리_테스트() throws Exception{
        //given //when
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<MemberGroup> ownerGroup1 = memberGroupRepository.findMyGroupByMemberId(member1.getId(), pageable);
        Slice<MemberGroup> ownerGroup2 = memberGroupRepository.findMyGroupByMemberId(member2.getId(), pageable);
        Slice<MemberGroup> joinGroup1 = memberGroupRepository.findMyGroupByMemberId(member3.getId(), pageable);
        Slice<MemberGroup> waitGroup1 = memberGroupRepository.findMyGroupByMemberId(member4.getId(), pageable);
        //then

        assertThat(ownerGroup1.getContent().size()).isEqualTo(2);
        assertThat(ownerGroup2.getContent().size()).isEqualTo(1);
        assertThat(joinGroup1.getContent().size()).isEqualTo(1);
        assertThat(waitGroup1.getContent().size()).isEqualTo(0);
    }


    @Test
    public void findJoinMemberByGroupId_쿼리_테스트() throws Exception{
        //given //when
        List<MemberGroup> group1members = memberGroupRepository.findJoinMemberByGroupId(group1.getId());
        List<MemberGroup> group2members = memberGroupRepository.findJoinMemberByGroupId(group2.getId());

        //then
        assertThat(group1members.size()).isEqualTo(2);
        assertThat(group2members.size()).isEqualTo(1);
    }

    @Test
    public void findByMemberIdAndGroupId_쿼리_테스트() throws Exception{
        //given //when
        Optional<MemberGroup> find = memberGroupRepository.findByMemberIdAndGroupId(member1.getId(), group1.getId());
        Optional<MemberGroup> notfind = memberGroupRepository.findByMemberIdAndGroupId(member1.getId(), group2.getId());
        //then

        assertThat(find.isEmpty()).isFalse();
        assertThat(notfind.isEmpty()).isTrue();
    }

    @Test
    public void findAllByGroupId_쿼리_테스트() throws Exception{
        //given //when
        List<MemberGroup> group1Find = memberGroupRepository.findAllByGroupId(group1.getId());
        List<MemberGroup> group2Find = memberGroupRepository.findAllByGroupId(group2.getId());

        //then
        assertThat(group1Find.size()).isEqualTo(3);
        assertThat(group2Find.size()).isEqualTo(1);
    }

    @Test
    public void findGroupOwnerByGroupId_쿼리_테스트() throws Exception{
        //given //when
        MemberGroup group1owner = memberGroupRepository.findGroupOwnerByGroupId(group1.getId())
                .orElseThrow(MemberNotExistException::new);
        MemberGroup group2owner = memberGroupRepository.findGroupOwnerByGroupId(group2.getId())
                .orElseThrow(MemberNotExistException::new);
        //then

        assertThat(group1owner.getAuthority()).isEqualTo(Authority.OWNER);
        assertThat(group1owner.getMember().getUsername()).isEqualTo(member1.getUsername());
        assertThat(group2owner.getAuthority()).isEqualTo(Authority.OWNER);
        assertThat(group2owner.getMember().getUsername()).isEqualTo(member2.getUsername());
    }

}