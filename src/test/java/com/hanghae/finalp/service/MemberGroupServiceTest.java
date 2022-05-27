package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorJoinException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorWaitException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.etc.DuplicationRequestException;
import com.hanghae.finalp.config.exception.customexception.etc.MaxNumberException;
import com.hanghae.finalp.entity.*;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberGroupServiceTest {
    @InjectMocks MemberGroupService memberGroupService;

    @Mock ChatMemberRepository chatMemberRepository;
    @Mock MemberGroupRepository memberGroupRepository;
    @Mock ChatRoomRepository chatRoomRepository;
    @Mock NoticeRepository noticeRepository;

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Group group1;
    private Group group2;
    private Group group3;
    private MemberGroup memberGroupOWNER;
    private MemberGroup memberGroupJOIN;
    private MemberGroup memberGroupWAIT;
    private MemberGroup memberGroupMaxMember;
    private Chatroom chatroom;

    @BeforeEach
    public void init() {
        member1 = Member.createMember("kakaoId1", "userA", "image1");
        member2 = Member.createMember("kakaoId2", "userB", "image2");
        member3 = Member.createMember("kakaoId3", "userC", "image3");
        member4 = Member.createMember("kakaoId4", "userD", "image4");

        group1 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");
        group2 = Group.createGroup("title2", "desc", 1, "방배",
                "image", member2, 999L, "2022.01.07", "2022.02.09");
        group3 = Group.createGroup("title1", "desc1", 5, "서초",
                "image1", member1, 999L, "2022.01.07", "2022.02.09");


        memberGroupOWNER = MemberGroup.createMemberGroup(Authority.OWNER, member3, group1, 999L);
        memberGroupJOIN = MemberGroup.createMemberGroup(Authority.JOIN, member4, group1, 999L);
        memberGroupWAIT = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
        memberGroupMaxMember = MemberGroup.createMemberGroup(Authority.WAIT, member4, group2, 999L);

        chatroom = Chatroom.createChatroomByGroup("title", member1);
    }


    @Nested
    class applyGroup {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findGroupOwnerByGroupId(anyLong()))
                    .willReturn(Optional.of(memberGroupOWNER));
            //when
            memberGroupService.applyGroup(1L, "username", 3L);
            //then
        }

        @Test
        public void 중복된_요청_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupWAIT));
//            given(memberGroupRepository.findGroupOwnerByGroupId(anyLong()))
//                    .willReturn(Optional.of(memberGroupOWNER));
            //when //then
            assertThatThrownBy(() -> memberGroupService.applyGroup(1L, "username", 3L))
                    .isInstanceOf(DuplicationRequestException.class);
        }
    }

    @Nested
    class approveGroup {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupWAIT));
            given(chatRoomRepository.findById(anyLong()))
                    .willReturn(Optional.of(chatroom));
            //when
            memberGroupService.approveGroup(1L, 2L, 3L);
            //then
            assertThat(memberGroupWAIT.getAuthority()).isEqualTo(Authority.JOIN);
        }

        @Test
        public void OWNER_권한X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then
            assertThatThrownBy(() -> memberGroupService.approveGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorOwnerException.class);
        }
        @Test
        public void 대기중인_사람X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then
            assertThatThrownBy(() -> memberGroupService.approveGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorWaitException.class);
        }

        @Test
        public void 그룹에_남은_자리가_없을때() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupMaxMember));
            //when //then
            assertThatThrownBy(() -> memberGroupService.approveGroup(1L, 2L, 3L))
                    .isInstanceOf(MaxNumberException.class);
        }
    }

    @Nested
    class denyGroup {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupWAIT));
            //when
            memberGroupService.denyGroup(1L, 2L, 3L);
            //then
        }

        @Test
        public void OWNER_권한X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
//            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
//                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then
            assertThatThrownBy(() -> memberGroupService.denyGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorOwnerException.class);
        }

        @Test
        public void 대기중인_사람X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then
            assertThatThrownBy(() -> memberGroupService.denyGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorWaitException.class);
        }
    }

    @Nested
    class banGroup {

        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
            given(chatMemberRepository.findByMemberIdAndChatroomId(anyLong(), anyLong()))
                    .willReturn(Optional.of(Mockito.mock(ChatMember.class)));
            given(chatRoomRepository.findById(anyLong()))
                    .willReturn(Optional.of(chatroom));
            //when
            int beforeMemberCount = memberGroupJOIN.getGroup().getMemberCount();
            memberGroupService.banGroup(1L, 2L, 3L);
            //then

            assertThat(memberGroupJOIN.getGroup().getMemberCount()).isEqualTo(beforeMemberCount - 1);
        }

        @Test
        public void OWNER_권한X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupJOIN));
//            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
//                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then
            assertThatThrownBy(() -> memberGroupService.banGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorOwnerException.class);
        }

        @Test
        public void 대기중인_사람X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(1L, 2L))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(memberGroupRepository.findByMemberIdAndGroupId(3L, 2L))
                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then
            assertThatThrownBy(() -> memberGroupService.banGroup(1L, 2L, 3L))
                    .isInstanceOf(AuthorJoinException.class);
        }
    }


    @Nested
    class cancelApplyGroup {

        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then

            memberGroupService.cancelApplyGroup(1L, 2L);
        }

        @Test
        public void 자신이_대기중이_아닐때() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then

            assertThatThrownBy(() -> memberGroupService.cancelApplyGroup(1L, 2L))
                    .isInstanceOf(AuthorWaitException.class);
        }

        @Test
        public void entity가_존재X_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.empty());
            //when //then

            assertThatThrownBy(() -> memberGroupService.cancelApplyGroup(1L, 2L))
                    .isInstanceOf(MemberGroupNotExistException.class);
        }
    }

    @Nested
    class exitGroup {

        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupJOIN));
            given(chatMemberRepository.findByMemberIdAndChatroomId(anyLong(), anyLong()))
                    .willReturn(Optional.of(Mockito.mock(ChatMember.class)));
            //when
            int beforeMemberCount = memberGroupJOIN.getGroup().getMemberCount();
            memberGroupService.exitGroup(1L, 2L);
            //then
            assertThat(memberGroupJOIN.getGroup().getMemberCount()).isEqualTo(beforeMemberCount - 1);
        }

        @Test
        public void 가입되어있지않은_그룹일때() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupWAIT));
//            given(chatMemberRepository.findByMemberIdAndChatroomId(anyLong(), anyLong()))
//                    .willReturn(Optional.of(Mockito.mock(ChatMember.class)));
            //when
            assertThatThrownBy(() -> memberGroupService.exitGroup(1L, 2L))
                    .isInstanceOf(AuthorJoinException.class);
            //then
        }
    }

    @Nested
    class setlocation {

        @Test
        public void 성공() throws Exception{
            //given
            MemberGroupDto.LocationReq locationReq = new MemberGroupDto.LocationReq("123", "456", "addr");
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when
            memberGroupService.setlocation(1L, 2L, locationReq);
            //then
            assertThat(memberGroupJOIN.getStartLocationX()).isEqualTo(locationReq.getStartLocationX());
            assertThat(memberGroupJOIN.getStartLocationY()).isEqualTo(locationReq.getStartLocationY());
            assertThat(memberGroupJOIN.getStartAddress()).isEqualTo(locationReq.getStartAddress());
        }

        @Test
        public void entity가_존재X_실패() throws Exception{
            //given
            MemberGroupDto.LocationReq locationReq = new MemberGroupDto.LocationReq("123", "456", "addr");
            given(memberGroupRepository.findByMemberIdAndGroupId(anyLong(), anyLong()))
                    .willReturn(Optional.empty());
            //when //then
            assertThatThrownBy(() -> memberGroupService.setlocation(1L, 2L, locationReq))
                    .isInstanceOf(MemberGroupNotExistException.class);
        }


    }


}