package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.etc.WebClientException;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.dto.other.KakaoApiDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.CafeRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class CafeServiceTest {
    private String kakao_apikey = "5a0f454edcfa03449fa61a887a45de32";

    @InjectMocks CafeService cafeService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebClient kakaoWebClient;
    @Mock CafeRepository cafeRepository;
    @Mock MemberGroupRepository memberGroupRepository;

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Group group1;
    private Group group2;
    private Group group3;
    private MemberGroup memberGroup3;
    private MemberGroup memberGroup4;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        member4 = Member.createMember("kakaoId4", "userD", "image4");

        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 5, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");
        group3 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group1.setIdForTest(5L);
        group1.setIdForTest(6L);
        group1.setIdForTest(7L);

        memberGroup3 = MemberGroup.createMemberGroup(Authority.OWNER, member3, group1, 999L);
        memberGroup4 = MemberGroup.createMemberGroup(Authority.JOIN, member4, group1, 999L);

    }

    @Nested
    class selectCafe {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong())).
                    willReturn(Optional.of(memberGroup3));
            willDoNothing().given(cafeRepository).deleteByGroupId(anyLong());
            //when
            CafeDto.CreateReq request =
                    new CafeDto.CreateReq("locationName", "127.12", "37.12", "서초");
            cafeService.selectCafe(1L, request, 2L);
            //then
        }

        @Test
        public void entity_존재X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong())).
                    willReturn(Optional.empty());
            //when
            CafeDto.CreateReq request =
                    new CafeDto.CreateReq("locationName", "127.12", "37.12", "서초");
            //then
            Assertions.assertThatThrownBy(() -> cafeService.selectCafe(1L, request, 2L))
                    .isInstanceOf(MemberGroupNotExistException.class);
        }

        @Test
        public void OWNER가_아니여서_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong())).
                    willReturn(Optional.of(memberGroup4));
            //when
            CafeDto.CreateReq request =
                    new CafeDto.CreateReq("locationName", "127.12", "37.12", "서초");
            //then
            Assertions.assertThatThrownBy(() -> cafeService.selectCafe(1L, request, 2L))
                    .isInstanceOf(AuthorOwnerException.class);
        }


    }

    @Nested
    class deleteCafe {
        @Test
        public void 성공() throws Exception{
            //given //when
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong())).
                    willReturn(Optional.of(memberGroup3));
            //then
            cafeService.deleteCafe(1L, 2L);
        }

        @Test
        public void entity_존재X_실패() throws Exception{
            //given //when
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong())).
                    willReturn(Optional.empty());
            //then
            Assertions.assertThatThrownBy(() -> cafeService.deleteCafe(1L, 2L))
                    .isInstanceOf(MemberGroupNotExistException.class);
        }

        @Test
        public void OWNER가_아니여서_실패() throws Exception{
            //given //when
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong())).
                    willReturn(Optional.of(memberGroup4));
            //then
            Assertions.assertThatThrownBy(() -> cafeService.deleteCafe(1L, 2L))
                    .isInstanceOf(AuthorOwnerException.class);
        }
    }

    @Nested
    class getRecoCafe {
//        @Test
        public void 성공() throws Exception {
            //given //when

            // WebClient mocking test 실패....

        }
    }

}