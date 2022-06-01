package com.hanghae.finalp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.GroupRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
public class GroupIntegrationTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberGroupRepository memberGroupRepository;
    @Autowired GroupRepository groupRepository;

    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired JwtTokenUtils jwtTokenUtils;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;

    Member member;
    Group group;

    Chatroom chatroom;
    String accessToken;

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Group group1;
    private Group group2;

    @BeforeEach
    public void initToken() throws Exception{
        member = Member.createMember("kakaoId", "username", "imageUrl");
        memberRepository.save(member);
        accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());

        chatroom = Chatroom.createChatroomByGroup("chatrooomTitle", member);
        chatRoomRepository.save(chatroom);

        group = Group.createGroup("title", "desc", 5, "addr",
                "imageUrl", member, chatroom.getId(), "2022.07.13", "2022.08.23");
        groupRepository.save(group);

        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        member4 = Member.createMember("kakaoId4", "userD", "image4");
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        group1 = Group.createGroup("new group title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("new group title2", "desc", 5, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");
        groupRepository.save(group1);
        groupRepository.save(group2);

        MemberGroup memberGroup3 = MemberGroup.createMemberGroup(Authority.JOIN, member3, group1, 999L);
        MemberGroup memberGroup4 = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
        memberGroupRepository.save(memberGroup3);
        memberGroupRepository.save(memberGroup4);
        em.flush();
        em.clear();

        em.flush();
        em.clear();
    }

    @Nested
    class myGroupList {
        @Test
        public void 성공() throws Exception{
            //given
            PageRequest pagealbe = PageRequest.of(0, 5);
            GroupDto.SimpleRes simpleRes = new GroupDto.SimpleRes(group);

            //when //then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/my")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(pagealbe))
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]").value(simpleRes));
        }

        @Test
        public void 내_그룹이_없을때() throws Exception{
            //given
            PageRequest pagealbe = PageRequest.of(0, 5);
            groupRepository.deleteById(group.getId());
            //when //then

            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/my")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(pagealbe))
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0]").doesNotExist());
        }
    }

    @Nested
    class createGroup {
        // multipart=form data 알아보기
    }

    @Nested
    class deleteGroup {
        @Test
        public void 성공() throws Exception{
            //given //when
            mockMvc.perform(MockMvcRequestBuilders.post("/api/groups/{groupId}/delete",group.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
            // then
            Optional<Group> findGroup = groupRepository.findById(group.getId());
            assertThat(findGroup.isEmpty()).isTrue();
        }

        @Test
        public void 잘못된_groupId() throws Exception{
            //given //when
            mockMvc.perform(MockMvcRequestBuilders.post("/api/groups/{groupId}/delete", "0")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isBadGateway());
            //then
        }
    }

    @Nested
    class patchReq {
        // multipart=form data 알아보기
    }

    @Nested
    class getGroupList {
        @Test
        public void 검색어X_주소필터X() throws Exception{
            //given
            PageRequest pageable = PageRequest.of(0, 10);
            //when//then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pageable))
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        public void 검색어O_주소필터X() throws Exception{
            //given
            PageRequest pageable = PageRequest.of(0, 10);
            //when//then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(pageable))
                            .queryParam("title", "new")
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect((MockMvcResultMatchers.jsonPath("$.content.length()").value(2)));
        }

        @Test
        public void 검색어X_주소필터O() throws Exception{
            //given
            PageRequest pageable = PageRequest.of(0, 10);
            //when//then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(pageable))
                            .queryParam("roughAddress", "서초")
                            .queryParam("roughAddress", "방배")
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect((MockMvcResultMatchers.jsonPath("$.content.length()").value(2)));
        }

        @Test
        public void 검색어O_주소필터O() throws Exception{
            //given
            PageRequest pageable = PageRequest.of(0, 10);
            //when//then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .content(objectMapper.writeValueAsString(pageable))
                            .queryParam("title", "new")
                            .queryParam("roughAddress", "서초")
                            .queryParam("roughAddress", "방배")
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect((MockMvcResultMatchers.jsonPath("$.content.length()").value(2)));
        }
    }

    @Nested
    class groupView {
        @Test
        public void 성공() throws Exception{
            //given//when//then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/{groupId}", group.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", accessToken)
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.groupTitle").value(group.getGroupTitle()));

        }

    }





}
