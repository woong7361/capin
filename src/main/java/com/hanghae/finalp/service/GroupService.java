package com.hanghae.finalp.service;

import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final MemberGroupRepository memberGroupRepository;

    public Slice<GroupDto.SimpleRes> getMyGroupList(Long memberId, Pageable pageable) {
        Slice<MemberGroup> myGroupByMember = memberGroupRepository.findMyGroupByMemberId(memberId, pageable);
        return myGroupByMember.map(GroupDto.SimpleRes::new);
    }
}
