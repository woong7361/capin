package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks GroupService groupService;

    @Mock MemberGroupRepository memberGroupRepository;
    @Mock GroupRepository groupRepository;
    @Mock MemberRepository memberRepository;
    @Mock S3Service s3Service;
    @Mock ChatRoomRepository chatRoomRepository;
    @Mock Pageable pageableMock;


    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Group group1;
    private Group group2;
    private MemberGroup memberGroupJOIN;
    private MemberGroup memberGroupWAIT;
    private MemberGroup memberGroupOWNER;

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

        memberGroupJOIN = MemberGroup.createMemberGroup(Authority.JOIN, member3, group1, 999L);
        memberGroupWAIT = MemberGroup.createMemberGroup(Authority.WAIT, member4, group1, 999L);
        memberGroupOWNER = MemberGroup.createMemberGroup(Authority.OWNER, member4, group1, 999L);
    }

    @Nested
    class getMyGroupList {

        @Test
        public void 성공() throws Exception{
            //given //when
            Slice pro= Mockito.mock(Slice.class);
            SliceImpl<MemberGroup> memberGroups = new SliceImpl<>(List.of(memberGroupJOIN));

            given(memberGroupRepository.findMyGroupByMemberId(anyLong(), any(PageRequest.class)))
                    .willReturn(memberGroups);

            //then
            Slice<GroupDto.SimpleRes> result = groupService.getMyGroupList(3L, PageRequest.of(0, 10));
            assertThat(result.getContent().get(0).getGroupTitle()).isEqualTo(memberGroupJOIN.getGroup().getGroupTitle());
        }

        @Test
        public void 내_그룹이_비어있을때() throws Exception{
            //given //when
            Slice pro= Mockito.mock(Slice.class);
            SliceImpl<MemberGroup> memberGroups = new SliceImpl<>(List.of());

            given(memberGroupRepository.findMyGroupByMemberId(anyLong(), any(PageRequest.class)))
                    .willReturn(memberGroups);

            //then
            Slice<GroupDto.SimpleRes> result = groupService.getMyGroupList(3L, PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    class createGroup {

        @Test
        public void 성공() throws Exception{
            //given //when
            Chatroom mockChatroom = Mockito.mock(Chatroom.class);
            MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            Group group = Group.createGroup(createReq, "fileUri", member1, 999L);

            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            given(s3Service.uploadFile(any(MultipartFile.class))).willReturn("fileUri");
            given(chatRoomRepository.save(any(Chatroom.class)))
                    .willReturn(mockChatroom);
            given(groupRepository.save(any(Group.class))).willReturn(group);

            //then
            GroupDto.SimpleRes result = groupService.createGroup(1L, createReq, mockMultipartFile);

            assertThat(result.getGroupTitle()).isEqualTo(group.getGroupTitle());
            assertThat(result.getImageUrl()).isEqualTo(group.getImageUrl());
            assertThat(result.getMaxMemberCount()).isEqualTo(group.getMaxMemberCount());
        }

        @Test
        public void member가_존재하지않아_실패() throws Exception{
            //given //when
            Chatroom mockChatroom = Mockito.mock(Chatroom.class);
            MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            Group group = Group.createGroup(createReq, "fileUri", member1, 999L);

            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            //then
            assertThatThrownBy(() -> groupService.createGroup(1L, createReq, mockMultipartFile))
                    .isInstanceOf(MemberNotExistException.class);

        }

        @Test
        public void multipartFile이_없을경우() throws Exception{
            //given //when
            Chatroom mockChatroom = Mockito.mock(Chatroom.class);
            MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            Group group = Group.createGroup(createReq, "fileUri", member1, 999L);

            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member1));
            given(s3Service.uploadFile(null)).willReturn(null);
            given(chatRoomRepository.save(any(Chatroom.class)))
                    .willReturn(mockChatroom);
            given(groupRepository.save(any(Group.class))).willReturn(group);

            //then
            GroupDto.SimpleRes result = groupService.createGroup(1L, createReq, null);
            assertThat(result.getImageUrl()).isEqualTo("https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/groupDefaultImg.png");
        }
    }

    @Nested
    class deleteGroup {
        @Test
        public void 성공() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupOWNER));
            //when //then
            groupService.deleteGroup(1L, 2L);
        }

        @Test
        public void AUTHORITY_JOIN_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then
            assertThatThrownBy(() -> groupService.deleteGroup(1L, 2L))
                    .isInstanceOf(AuthorOwnerException.class);
        }

        @Test
        public void AUTHORITY_WAIT_실패() throws Exception{
            //given
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then
            assertThatThrownBy(() -> groupService.deleteGroup(1L, 2L))
                    .isInstanceOf(AuthorOwnerException.class);
        }
    }

    @Nested
    class patchGroup {
        @Test
        public void 성공() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupOWNER));
            given(s3Service.uploadFile(any(MultipartFile.class)))
                    .willReturn("imageUri");
            //when
            groupService.patchGroup(1L, 2L, createReq, mockFile);

            //then
            assertThat(memberGroupOWNER.getGroup().getImageUrl()).isEqualTo("imageUri");
            assertThat(memberGroupOWNER.getGroup().getGroupTitle()).isEqualTo(createReq.getGroupTitle());
        }

        @Test
        public void Entity가_존재하지않아_실패() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.empty());
            //when //then
            assertThatThrownBy(() -> groupService.patchGroup(1L, 2L, createReq, mockFile))
                    .isInstanceOf(MemberGroupNotExistException.class);
        }

        @Test
        public void AUTHORITY_JOIN_실패() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupJOIN));
            //when //then
            assertThatThrownBy(() -> groupService.patchGroup(1L, 2L, createReq, mockFile))
                    .isInstanceOf(AuthorOwnerException.class);
        }

        @Test
        public void AUTHORITY_WAIT_실패() throws Exception{
            //given
            MultipartFile mockFile = Mockito.mock(MultipartFile.class);
            GroupDto.CreateReq createReq = new GroupDto.CreateReq("title", "desc",
                    10, "서초", "1988.01.07", "1988.08.01");
            given(memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(anyLong(), anyLong()))
                    .willReturn(Optional.of(memberGroupWAIT));
            //when //then
            assertThatThrownBy(() -> groupService.patchGroup(1L, 2L, createReq, mockFile))
                    .isInstanceOf(AuthorOwnerException.class);
        }
    }



    @Nested
    class getSearchGroupList {
        @Test
        public void 키워드X_지역필터X_성공() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<>(List.of(group1, group2));
            given(groupRepository.findAllToSlice(any(Pageable.class))).willReturn(slice);
            //when
            Slice<GroupDto.SimpleRes> searchGroupList = groupService.getSearchGroupList(null, null, PageRequest.of(0, 6));
            //then
            assertThat(searchGroupList.getContent().size()).isEqualTo(2);
        }

        @Test
        public void 키워드O_지역필터X_성공() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<>(List.of(group1, group2));
            given(groupRepository.findAllByGroupTitleContaining(anyString(), any(Pageable.class))).willReturn(slice);
            //when
            Slice<GroupDto.SimpleRes> searchGroupList
                    = groupService.getSearchGroupList("title", null, PageRequest.of(0, 6));
            //then
            assertThat(searchGroupList.getContent().size()).isEqualTo(2);
        }

        @Test
        public void 키워드X_지역필터O_성공() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<>(List.of(group1, group2));
            List<String> addressList = List.of("서초");
            given(groupRepository.findAllByRoughAddressIn(anyList(), any(Pageable.class))).willReturn(slice);
            //when
            Slice<GroupDto.SimpleRes> searchGroupList = groupService.getSearchGroupList(null, addressList, PageRequest.of(0, 6));
            //then
            assertThat(searchGroupList.getContent().size()).isEqualTo(2);
        }

        @Test
        public void 키워드O_지역필터O_성공() throws Exception{
            //given
            SliceImpl slice = new SliceImpl<>(List.of(group1, group2));
            List<String> addressList = List.of("서초");

            given(groupRepository.findAllByGroupTitleContainingAndRoughAddressIn(anyString(), anyList(), any(Pageable.class)))
                    .willReturn(slice);
            //when
            Slice<GroupDto.SimpleRes> searchGroupList =
                    groupService.getSearchGroupList("title", addressList, PageRequest.of(0, 6));
            //then
            assertThat(searchGroupList.getContent().size()).isEqualTo(2);
        }

    }



}