package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks MemberService memberService;

    @Mock MemberRepository memberRepository;
    @Mock S3Service s3Service;

    private Member member1;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
    }

    @Nested
    class getMyProfile {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            //when
            MemberDto.ProfileRes profile = memberService.getMyProfile(1L);
            //then
            assertThat(profile.getUsername()).isEqualTo(member1.getUsername());
            assertThat(profile.getImageUrl()).isEqualTo(member1.getImageUrl());
        }

        @Test
        public void entity존재_X_실패() throws Exception{
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when //then
            assertThatThrownBy(() -> memberService.getMyProfile(1L))
                    .isInstanceOf(MemberNotExistException.class);
        }
    }

    @Nested
    class editMyProfile {

        @Test
        public void 성공() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            given(s3Service.uploadFile(mockFile)).willReturn("imageUri");
            //when
            MemberDto.ProfileRes profile = memberService.editMyProfile("editName", mockFile, 1L);
            //then
            assertThat(profile.getUsername()).isEqualTo("editName");
            assertThat(profile.getImageUrl()).isEqualTo("imageUri");
            assertThat(member1.getUsername()).isEqualTo("editName");
            assertThat(member1.getImageUrl()).isEqualTo("imageUri");
        }

        @Test
        public void multipartFile이_null일때() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            given(s3Service.uploadFile(null)).willReturn(null);
            //when
            MemberDto.ProfileRes profile = memberService.editMyProfile("editName", null, 1L);
            //then
            assertThat(profile.getUsername()).isEqualTo("editName");
            assertThat(profile.getImageUrl()).isEqualTo("https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/memberDefaultImg.png");
            assertThat(member1.getUsername()).isEqualTo("editName");
            assertThat(member1.getImageUrl()).isEqualTo("https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/memberDefaultImg.png");
        }

        @Test
        public void entity존재_X_실패() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when
            assertThatThrownBy(() -> memberService.editMyProfile("editName", mockFile, 1L))
                    .isInstanceOf(MemberNotExistException.class);
        }
    }

    @Nested
    class deleteMember {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            //when //then
            memberService.deleteMember(1L);
        }

        @Test
        public void entity존재_X_실패() throws Exception{
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());
            //when //then
            assertThatThrownBy(() -> memberService.deleteMember(1L))
                    .isInstanceOf(MemberNotExistException.class);
        }
    }

}