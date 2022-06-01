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
import com.hanghae.finalp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private final ChatMemberRepository chatMemberRepository;


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
        if(imageUrl == null) imageUrl = "https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/groupDefaultImg.png";
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
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(memberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            log.debug("custom log:: delete group is required owner authority");
            throw new AuthorOwnerException();
        }
        s3Service.deleteFile(memberGroup.getGroup().getImageUrl());
        groupRepository.deleteById(memberGroup.getGroup().getId());

        log.debug("custom log:: chatroom 관련 logic");
        Long chatroomId = memberGroup.getChatroomId();
        chatRoomRepository.deleteById(chatroomId);
    }

    /**
     * 그룹 수정
     */
    @Transactional
    public void patchGroup(Long memberId, Long groupId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(memberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            log.debug("custom log:: patch group is required owner authority");
            throw new AuthorOwnerException();
        }
        Group group = memberGroup.getGroup();

        s3Service.deleteFile(group.getImageUrl());
        String imageUrl = s3Service.uploadFile(multipartFile);
        if(imageUrl == null) imageUrl = "https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/groupDefaultImg.png";

        group.patch(createReq, imageUrl);
    }


    /**
     * 그룹 검색 리스트 가져오기
     */
    @Transactional
    public Slice<GroupDto.SimpleRes> getSearchGroupList(String title, List<String> addressList, Pageable pageable) {

        Slice<Group> groups;
        if (title == null && addressList == null){
            groups = groupRepository.findAllToSlice(pageable);
        } else if (title == null){
            groups = groupRepository.findAllByRoughAddressIn(addressList, pageable);
        } else if (addressList == null) {
            groups = groupRepository.findAllByGroupTitleContaining(title, pageable);
        }else {
            groups = groupRepository.findAllByGroupTitleContainingAndRoughAddressIn(title, addressList, pageable);
        }
        return groups.map(GroupDto.SimpleRes::new);
    }


    /**
     * 특정 그룹 불러오기
     */
    @Transactional
    public GroupDto.SpecificRes groupView(Long groupId) {
        List<MemberGroup> memberGroupList = memberGroupRepository.findAllByGroupId(groupId);

        Stream<MemberDto.SpecificRes> ownerStream = memberGroupList.stream()
                .filter(mg -> mg.getAuthority().equals(Authority.OWNER))
                .map(mg -> mg.getMember())
                .map(MemberDto.OwnerSpecificRes::new)
                .map(MemberDto.SpecificRes::new);

        Stream<MemberDto.SpecificRes> joinStream = memberGroupList.stream()
                .filter(mg -> mg.getAuthority().equals(Authority.JOIN))
                .map(mg -> mg.getMember())
                .sorted(Comparator.comparing(Member::getUsername))
                .map(MemberDto.JoinSpecificRes::new)
                .map(MemberDto.SpecificRes::new);

        List<MemberDto.SpecificRes> specificResList = Stream.concat(ownerStream, joinStream)
                .collect(Collectors.toList());

        Group group = groupRepository.findById(groupId).orElseThrow(GroupNotExistException::new);
        return new GroupDto.SpecificRes(group, specificResList);
    }


}