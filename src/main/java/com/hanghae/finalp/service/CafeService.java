package com.hanghae.finalp.service;

import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.CafeRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final MemberGroupRepository memberGroupRepository;


    @Transactional
    public void selectCafe(Long memberId, CafeDto.Reqeust request, Long groupId){
        //카페 선택 버튼이 오너일 경우에만 보이는지 , 버튼은 누구나 볼 수 있고 오너만 누를 수 있게 할 것인지 정해야함
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new RuntimeException("not exist member"));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new RuntimeException("not owner");
        }
        Cafe.createCafe(request.getLocationName(), request.getLocationX(), request.getLocationY(), request.getAddress(), memberGroup.getGroup());
    }

    @Transactional
    public void deleteCafe(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new RuntimeException("not exist member"));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new RuntimeException("not owner");
        }
        cafeRepository.deleteByGroupId(groupId);
    }
}
