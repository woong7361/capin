package com.hanghae.finalp.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.controller.TestController;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.util.JwtTokenUtils;
import io.jsonwebtoken.Jwt;
import okhttp3.MultipartBody;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class MemberIntegrationTest {
//    @Autowired MemberRepository memberRepository;
//    @Autowired JwtTokenUtils jwtTokenUtils;
//
//    @Autowired MockMvc mockMvc;
//    @Autowired ObjectMapper objectMapper;
//    @Autowired EntityManager em;
//
//    Member member;
//    String accessToken;
//
//    @BeforeEach
//    public void initToken() throws Exception{
//        member = Member.createMember("kakaoId", "username", "imageUrl");
//        memberRepository.save(member);
//        System.out.println("member.getId() = " + member.getId());
//        accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());
//        em.flush();
//        em.clear();
//    }
//
//
//    @Test
//    public void getMyProfile() throws Exception{
//        //given
//        MemberDto.ProfileRes profileRes =
//                new MemberDto.ProfileRes(member.getId(), member.getUsername(), member.getImageUrl());
//
//        //when //then
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/profile")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", accessToken)
//                        .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(profileRes)));
//    }
//
//
//    @Test
//    public void memberEdit() throws Exception{
//        //given
//        MockMultipartFile file = new MockMultipartFile("image", "mock", "image/jpeg", "<<jpeg data>>".getBytes());
//        MockPart mockPart = new MockPart("username", "username", "fixUsername".getBytes());
//        //when
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/profile/edit")
//                        .file(file)
//                        .part(mockPart)
//                        .header("Authorization", accessToken)
//                        .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("fixUsername"));
//        //then
//    }
//
//
//}
