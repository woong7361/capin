package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.code.ErrorMessageCode;
import com.hanghae.finalp.config.exception.customexception.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.CafeRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.AUTHORITY_ERROR_CODE;
import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.ENTITY_NOT_FOUND_CODE;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final MemberGroupRepository memberGroupRepository;


    @Transactional
    public void selectCafe(Long memberId, CafeDto.Reqeust request, Long groupId){
        //카페 선택 버튼이 오너일 경우에만 보이는지 , 버튼은 누구나 볼 수 있고 오너만 누를 수 있게 할 것인지 정해야함
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "memberGroup에 memberId가 존재하지 않는다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)) {
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 만들 수 없는 권한 입니다.");
        }
        //이전의 카페 삭제
        cafeRepository.deleteByGroupId(memberGroup.getGroup().getId());
        //group의 변경감지
        Cafe cafe = Cafe.createCafe(request.getLocationName(), request.getLocationX(), request.getLocationY(),
                request.getAddress(), memberGroup.getGroup());
    }

    @Transactional
    public void deleteCafe(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "memberGroup에 memberId가 존재하지 않는다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 지울 수 없는 권한 입니다.");
        }
        cafeRepository.deleteByGroupId(groupId);
    }
}
