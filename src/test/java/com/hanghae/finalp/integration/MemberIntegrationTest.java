package com.hanghae.finalp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
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
public class MemberIntegrationTest {
    @Autowired MemberRepository memberRepository;
    @Autowired JwtTokenUtils jwtTokenUtils;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityManager em;

    Member member;
    String accessToken;

    @BeforeEach
    public void initToken() throws Exception{
        member = Member.createMember("kakaoId", "username", "imageUrl");
        memberRepository.save(member);
        accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());
        em.flush();
        em.clear();
    }


    @Test
    public void getMyProfile() throws Exception{
        //given
        MemberDto.ProfileRes profileRes =
                new MemberDto.ProfileRes(member.getId(), member.getUsername(), member.getImageUrl());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(profileRes)));
    }

    @Test
    public void withdraw() throws Exception{
        //given //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .accept(MediaType.APPLICATION_JSON)
        );
        // then
        Optional<Member> findMember = memberRepository.findById(member.getId());
        assertThat(findMember.isEmpty()).isTrue();
    }

    //multipart-data test가 로컬에서는 작동되는데 배포 환경에서 실패 -> 이유 찾기

    @Test
    @Disabled
    public void memberEdit() throws Exception{
        //given
        MockMultipartFile file = new MockMultipartFile("image", "mock", "image/jpeg", "<<jpeg data>>".getBytes());
        MockPart mockPart = new MockPart("username", "username", "fixUsername".getBytes());
        //when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/profile/edit")
                        .file(file)
                        .part(mockPart)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("fixUsername"));
        //then
    }


}
