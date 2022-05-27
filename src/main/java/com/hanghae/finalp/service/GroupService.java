package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.entity.GroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.GroupRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final MemberGroupRepository memberGroupRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final ChatRoomRepository chatRoomRepository;


    /**
     * 내 그룹리스트 가져오기
     */
    public Slice<GroupDto.SimpleRes> getMyGroupList(Long memberId, Pageable pageable) {
        Slice<MemberGroup> myGroupByMember = memberGroupRepository.findMyGroupByMemberId(memberId, pageable);
        return myGroupByMember
                .map(GroupDto.SimpleRes::new);
    }

    /**
     * 새로운 그룹 생성
     */
    @Transactional
    public GroupDto.SimpleRes createGroup(Long memberId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                MemberNotExistException::new);
        String imageUrl = s3Service.uploadFile(multipartFile);

        log.debug("custom log:: create group chatroom...");
        Chatroom groupChatroom = Chatroom.createChatroomByGroup(createReq.getGroupTitle(), member);
        chatRoomRepository.save(groupChatroom);
        Group group = Group.createGroup(createReq, imageUrl, member, groupChatroom.getId());
        groupRepository.save(group);

        return new GroupDto.SimpleRes(group);
    }


    /**
     * 그룹 삭제
     */
    @Transactional
    public void deleteGroup(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            log.debug("custom log:: delete group is required owner authority");
            throw new AuthorOwnerException();
        }

        s3Service.deleteFile(memberGroup.getGroup().getImageUrl());
        groupRepository.deleteById(memberGroup.getGroup().getId());
    }

    /**
     * 그룹 수정
     */
    @Transactional
    public void patchGroup(Long memberId, Long groupId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            log.debug("custom log:: patch group is required owner authority");
            throw new AuthorOwnerException();
        }
        Group group = memberGroup.getGroup();

        s3Service.deleteFile(group.getImageUrl());
        String imageUrl = s3Service.uploadFile(multipartFile);

        group.patch(createReq, imageUrl);
    }


    /**
     * 그룹 검색 리스트 가져오기
     */
    @Transactional
    public Slice<GroupDto.SimpleRes> getSearchGroupList(GroupDto.SearchReq searchReq, Pageable pageable) {

        Slice<Group> groups;
        if (searchReq == null){
            groups = groupRepository.findAllToSlice(pageable);
        } else if (searchReq.getTitle() == null){
            List<String> addressList = searchReq.getAddressList().stream().map(address -> address.getAddress()).collect(Collectors.toList());
            groups = groupRepository.findAllByRoughAddressIn(addressList, pageable);
        } else if (searchReq.getAddressList() == null) {
            groups = groupRepository.findAllByGroupTitleContaining(searchReq.getTitle(), pageable);
        }else {
            List<String> addressList = searchReq.getAddressList().stream().map(address -> address.getAddress()).collect(Collectors.toList());
            groups = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn(searchReq.getTitle(), addressList, pageable);
        }
        return groups.map(GroupDto.SimpleRes::new);
    }


    /**
     * 특정 그룹 불러오기
     */
    @Transactional
    public GroupDto.SpecificRes groupView(Long groupId) {

        List<Member> memberList = memberGroupRepository.findAllByGroupId(groupId).stream()
                .filter(mg -> !mg.getAuthority().equals(Authority.WAIT))
                .map(mg -> mg.getMember())
                .collect(Collectors.toList());

        List<MemberDto.ProfileRes> profileResList = memberList.stream()
                .map(MemberDto.ProfileRes::new)
                .collect(Collectors.toList());

        Group group = groupRepository.findById(groupId).orElseThrow(GroupNotExistException::new);

        return new GroupDto.SpecificRes(group, profileResList);
    }





}