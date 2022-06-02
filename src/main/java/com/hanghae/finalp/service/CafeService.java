package com.hanghae.finalp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.etc.WebClientException;
import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.dto.other.KakaoApiDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.CafeRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final WebClient kakaoWebClient;


    /**
     * 카페 선택(생성)
     */
    @Transactional
    public void selectCafe(Long memberId, CafeDto.CreateReq request, Long groupId){
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(memberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)) {
            throw new AuthorOwnerException();
        }
        //이전의 카페 삭제
        cafeRepository.deleteByGroupId(groupId);

        //group의 변경감지 - 이상하지만 일단 남겨두기
        Cafe cafe = Cafe.createCafe(request.getLocationName(), request.getLocationX(), request.getLocationY(),
                request.getAddress(), memberGroup.getGroup());
    }

    /**
     * 카페 삭제
     */
    @Transactional
    public void deleteCafe(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorOwnerException();
        }
        cafeRepository.deleteByGroupId(groupId);
    }


    /**
     * 카페 추천
     */
    public CafeDto.RecoRes getRecoCafe(Long groupId) {
        log.debug("get kakao local API");
        MemberGroupDto.Location location = findMid(groupId);

        log.debug("get kakao map scaping data");
        KakaoApiDto kakaoApiDto = getKakaoKeywordLocalApi(location);

        CafeDto.RecoRes cafeScrapInfo = getCafeScrapInfo(kakaoApiDto);
        return cafeScrapInfo;
    }


    /**
     * 카페 추천 - 메인
     */
    public CafeDto.RecoRes getRecoCafeForMain(Double locationX, Double locationY) {
        MemberGroupDto.Location location = new MemberGroupDto.Location(locationX, locationY);
        KakaoApiDto kakaoApiDto = getKakaoKeywordLocalApi(location);

        CafeDto.RecoRes cafeScrapInfo = getCafeScrapInfo(kakaoApiDto);
        return cafeScrapInfo;
    }


    //===================================================================================================//


    /**
     * 그룹내의 가운데 지점 가져오기
     */
    public MemberGroupDto.Location findMid(Long groupId) {
        List<MemberGroup> memberGroupList = memberGroupRepository.findJoinMemberByGroupId(groupId)
                .stream().filter(mg -> Optional.ofNullable(mg.getStartLocationX()).isPresent())
                .collect(Collectors.toList());

        MemberGroupDto.Location location = new MemberGroupDto.Location();

        memberGroupList.forEach((mg) -> location.addLocation(mg.getStartLocationX(), mg.getStartLocationY()));
        location.avg(memberGroupList.size());
        return location;
    }

    /**
     * 카카오 API에서 좌표주변의 카페 가져오기
     * 출처 - https://developers.kakao.com/docs/latest/ko/local/dev-guide
     */
    private KakaoApiDto getKakaoKeywordLocalApi(MemberGroupDto.Location location) {
        KakaoApiDto kakaoApiDto = kakaoWebClient.get()
                .uri(builder -> builder.path("/v2/local/search/keyword.json") //카카오 로컬- "키워드로 검색하기
//                        .queryParam("category_group_code", "CE7") //카테고리 그룹 코드
                                .queryParam("x", location.getLocationX().toString()) //longitude
                                .queryParam("y", location.getLocationY().toString()) //latitude
                                .queryParam("radius", 20000) //반경 단위(m) 최대 20000
                                .queryParam("size", 5) //추천 카페 수
                                .queryParam("query", "스터디 카페")
                        .queryParam("sort", "distance")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK,
                        clientResponse -> {
                            log.debug("kakao local api error statusCode: {}, string: {}", clientResponse.statusCode(), clientResponse.toString());
                            return clientResponse.createException()
                                    .flatMap(it -> Mono.error(new WebClientException()));
                        })
                .bodyToMono(KakaoApiDto.class)
                .block();
        return kakaoApiDto;
    }

    /**
     * 카카오맵에서 이미지와 코멘트, 별점 가져오기
     * 출처 - https://place.map.kakao.com/main/v/{id}
     */
    private CafeDto.RecoRes getCafeScrapInfo(KakaoApiDto kakaoApiDto) {
        CafeDto.RecoRes recoRes = new CafeDto.RecoRes(); //kakaoApiDto 정보 + 추가 정보 담음
        for (KakaoApiDto.Document doc  : kakaoApiDto.getDocuments()) {

            String block = WebClient.create()
                    .get()
                    .uri("https://place.map.kakao.com/main/v/" + doc.getId())
                    .headers(header -> {
                        header.setContentType(MediaType.APPLICATION_JSON);
                        header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                    })
                    .retrieve()
                    .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK,
                        clientResponse -> {
                            log.debug("kakao local api error statusCode: {}, string: {}", clientResponse.statusCode(), clientResponse.toString());
                            return clientResponse.createException()
                                    .flatMap(it -> Mono.error(new WebClientException()));
                    })
                    .bodyToMono(String.class)
                    .block();

            extractValue(recoRes, doc, block);
        }
        return recoRes;
    }

    /**
     * kakaoMap에서 필요한 값 추출
     */
    private void extractValue(CafeDto.RecoRes recoRes, KakaoApiDto.Document doc, String block) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(block);
            JsonNode basicInfo = jsonNode.get("basicInfo");
            JsonNode feedback = basicInfo.get("feedback");
            String mainphotourl = Optional.ofNullable(basicInfo.get("mainphotourl"))
                    .map(JsonNode::textValue).orElse("");

            int comntcnt = feedback.get("comntcnt").intValue();
            int scoresum = feedback.get("scoresum").intValue();
            int scorecnt = feedback.get("scorecnt").intValue();
            recoRes.getCafes().add(new CafeDto.RecoRes.CafeInfo(mainphotourl, comntcnt, scoresum, scorecnt, doc));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
