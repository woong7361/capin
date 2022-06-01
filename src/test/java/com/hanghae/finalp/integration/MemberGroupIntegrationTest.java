package com.hanghae.finalp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.entity.*;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import com.hanghae.finalp.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(connection = H2)
@Disabled
public class MemberGroupIntegrationTest {

    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired ChatMemberRepository chatMemberRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired MemberGroupRepository memberGroupRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenUtils jwtTokenUtils;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;

    Member member;
    String accessToken;
    Member member1;
    Member member2;
    Member member3;
    Member member4;
    Group group1;
    Group group2;
    Group myGroup;
    Chatroom groupChatroom1;
    Chatroom groupChatroom2;
    Chatroom groupChatroom3;

    @BeforeEach
    public void init() {
        member = Member.createMember("kakaoId", "username", "imageUrl");
        memberRepository.save(member);
        accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());

        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        member4 = Member.createMember("kakaoId4", "userD", "image4");
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        groupChatroom1 = Chatroom.createChatroomByGroup("group1", member1);
        groupChatroom2 = Chatroom.createChatroomByGroup("group2", member2);
        groupChatroom3 = Chatroom.createChatroomByGroup("group3", member3);
        chatRoomRepository.save(groupChatroom1);
        chatRoomRepository.save(groupChatroom2);
        chatRoomRepository.save(groupChatroom3);

        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, groupChatroom1.getId(), "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 5, "방배",
                "image", member, groupChatroom2.getId(), "2022.01.07", "2022.02.09");
        myGroup = Group.createGroup("title1", "desc1", 10, "서초",
                "image1", member, groupChatroom3.getId(), "2022.01.07", "2022.02.09");
        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(myGroup);

//        MemberGroup memberGroup3 = MemberGroup.createMemberGroup(Authority.JOIN, member3, group1, 999L);
//        MemberGroup memberGroup4 = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
//        memberGroupRepository.save(memberGroup3);
//        memberGroupRepository.save(memberGroup4);
        em.flush();
        em.clear();
    }

    @Nested
    class GroupApply {
        @Test
        public void 성공() throws Exception{
            //given //when
            mockMvc.perform(MockMvcRequestBuilders.post("/api/groups/{groupId}/apply", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            //then
            Authority memberAuthority = memberGroupRepository.findByMemberIdAndGroupId(member.getId(), group1.getId())
                    .map(MemberGroup::getAuthority)
                    .orElseThrow(MemberGroupNotExistException::new);
            assertThat(memberAuthority).isEqualTo(Authority.WAIT);
        }

        @Test
        public void 이미_가입_대기중인_그룹() throws Exception{
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member.getId(), group1.getId(), groupChatroom1.getId());
            memberGroupRepository.save(mg);
            //when //then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/groups/{groupId}/apply", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        public void 이미_가입한_그룹() throws Exception{
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member.getId(), group1.getId(), groupChatroom1.getId());
            memberGroupRepository.save(mg);
            //when //then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/groups/{groupId}/apply", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    class GroupApproval {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member1, myGroup, groupChatroom3.getId());
            memberGroupRepository.save(mg);
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/approval/{memberId}", myGroup.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Authority memberAuthority = memberGroupRepository.findByMemberIdAndGroupId(member1.getId(), myGroup.getId())
                    .map(MemberGroup::getAuthority)
                    .orElseThrow(MemberGroupNotExistException::new);
            assertThat(memberAuthority).isEqualTo(Authority.JOIN);
        }

        @Test
        public void 멤버가_꽉차있을때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member1, group2, groupChatroom2.getId());
            memberGroupRepository.save(mg);
            for (int i = 0; i < 4; i++) group2.plusMemberCount();
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/approval/{memberId}", group2.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
        @Test
        public void 대기중인_멤버가_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member1, group2, groupChatroom2.getId());
            memberGroupRepository.save(mg);
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/approval/{memberId}", group2.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class GroupDenial {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member1, myGroup, groupChatroom3.getId());
            memberGroupRepository.save(mg);
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/denial/{memberId}", myGroup.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Optional<MemberGroup> findMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(member1.getId(), myGroup.getId());
            assertThat(findMemberGroup.isEmpty()).isTrue();
        }

        @Test
        public void 대기중인_멤버가_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member1, myGroup, groupChatroom3.getId());
            memberGroupRepository.save(mg);
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/denial/{memberId}", myGroup.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class GroupBan {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member1, myGroup, groupChatroom3.getId());
            ChatMember chatMember = ChatMember.createChatMember(member1, groupChatroom3);
            chatMemberRepository.save(chatMember);
            memberGroupRepository.save(mg);
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/ban/{memberId}", myGroup.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Optional<MemberGroup> findMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(member1.getId(), myGroup.getId());
            assertThat(findMemberGroup.isEmpty()).isTrue();
        }

        @Test
        public void 가입중인_멤버가_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member1, myGroup, groupChatroom3.getId());
            memberGroupRepository.save(mg);
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/ban/{memberId}", myGroup.getId(), member1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class cancelApply {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/cancel", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Optional<MemberGroup> findMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(member.getId(), group1.getId());
            assertThat(findMemberGroup.isEmpty()).isTrue();
        }

        @Test
        public void 대기중이_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/cancel", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class exitGroup {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            ChatMember chatMember = ChatMember.createChatMember(member, groupChatroom1);
            chatMemberRepository.save(chatMember);
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/exit", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Optional<MemberGroup> findMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(member.getId(), group1.getId());
            assertThat(findMemberGroup.isEmpty()).isTrue();
        }

        @Test
        public void 대기중이_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            ChatMember chatMember = ChatMember.createChatMember(member1, groupChatroom1);
            chatMemberRepository.save(chatMember);
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/exit", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class locationSet {
        @Test
        public void 성공() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.JOIN, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            ChatMember chatMember = ChatMember.createChatMember(member, groupChatroom1);
            chatMemberRepository.save(chatMember);
            MemberGroupDto.LocationReq locationReq =
                    new MemberGroupDto.LocationReq("111.111", "222.222", "addr");
            //when
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/location", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(locationReq))
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            MemberGroup findMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(member.getId(), group1.getId())
                    .orElseThrow(MemberGroupNotExistException::new);
            assertThat(findMemberGroup.getStartLocationX()).isEqualTo(locationReq.getStartLocationX());
            assertThat(findMemberGroup.getStartLocationY()).isEqualTo(locationReq.getStartLocationY());
            assertThat(findMemberGroup.getStartAddress()).isEqualTo(locationReq.getStartAddress());
        }

        @Test
        public void 가입중이_아닐때() throws Exception {
            //given
            MemberGroup mg = MemberGroup.createMemberGroup(Authority.WAIT, member, group1, groupChatroom1.getId());
            memberGroupRepository.save(mg);
            MemberGroupDto.LocationReq locationReq =
                    new MemberGroupDto.LocationReq("111.111", "222.222", "addr");
            //when // then
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/groups/{groupId}/location", group1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(locationReq))
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }


    }
}
