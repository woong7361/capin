package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.CountNumberException;
import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
import com.hanghae.finalp.entity.*;
import com.hanghae.finalp.entity.dto.CrawlingDto;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.dto.KakaoApiDto;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.*;

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
    private final WebClient kakaoWebClient;


    public Slice<GroupDto.SimpleRes> getMyGroupList(Long memberId, Pageable pageable) {
        Slice<MemberGroup> myGroupByMember = memberGroupRepository.findMyGroupByMemberId(memberId, pageable);
        return myGroupByMember.map(GroupDto.SimpleRes::new);
    }

    @Transactional
    public GroupDto.SimpleRes createGroup(Long memberId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 memberId가 존재하지 않습니다."));
        String imageUrl = s3Service.uploadFile(multipartFile);

        Chatroom groupChatroom = Chatroom.createChatroomByGroup(createReq.getGroupTitle(), member);
        chatRoomRepository.save(groupChatroom);
        Group group = Group.createGroup(createReq, imageUrl, member, groupChatroom.getId());
        groupRepository.save(group);

        return new GroupDto.SimpleRes(group);
    }

    @Transactional
    public void deleteGroup(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 지울 수 없는 권한 입니다.");
        }

        s3Service.deleteFile(memberGroup.getGroup().getImageUrl());
        groupRepository.deleteById(memberGroup.getGroup().getId());
    }

    @Transactional
    public void patchGroup(Long memberId, Long groupId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 지울 수 없는 권한 입니다.");
        }
        //fetch join 필요
        Group group = memberGroup.getGroup();

        s3Service.deleteFile(group.getImageUrl());
        String imageUrl = s3Service.uploadFile(multipartFile);

        group.patch(createReq, imageUrl);
    }




    //------------------------------------------------------------------------------------

    //페이징
    @Transactional
    public Page<Group> getGroupList(Long groupId, Pageable pageable) {
        return groupRepository.findAllById(groupId, pageable);
    }

    //그룹 검색
    @Transactional
    public Page<Group> groupSearch(String searchKeyword, Pageable pageable) {
        return groupRepository.findByGroupTitleContaining(searchKeyword, pageable);
    }

    //특정 그룹 불러오기
    @Transactional
    public Slice<Group> groupView(Long groupId){
        return groupRepository.findMemberByGroupId(groupId);
    }

    //그룹 참가 신청
    @Transactional
    public void applyGroup(Long memberId, Long groupId) {
        //멤버가 이미 해당 그룹에 속해있는지 확인하기 -> memberGroup에 memberId, groupId 동시에 있는지 확인하면됨
        Optional<MemberGroup> memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId);

        //이미 그룹에 속해있는 경우-> 권한을 확인하기(권한이 반드시 있음)
        if (memberGroup.isPresent()) {
            Authority authority = memberGroup.get().getAuthority(); //get()할 경우 값이 null 이면 exception 반환함.이 경우 괜춘

            if (authority.equals(Authority.OWNER)) {
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "부적절한 접근입니다.");
            } else if (authority.equals(Authority.JOIN)) {
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페에 이미 가입중입니다.");
            } else if (authority.equals(Authority.WAIT)){
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "가입 승인을 대기 중입니다.");
            }
        }
        //그룹에 속하지 않은 경우
        //WAIT으로 memberGroup을 생성 -chatroodId는 승인시 따로 넣어줄 예정
        MemberGroup newMemberGroup = MemberGroup.createMemberGroup(Authority.WAIT, memberId, groupId, null);
        Group group= groupRepository.findById(groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
        group.getMemberGroups().add(newMemberGroup);
    }



    //그룹 참가자 승인
    @Transactional
    public void approveGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if(!Authority.OWNER.equals(myMemberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "승인을 할 수 없는 권한 입니다.");
        }

        //그사람도 같은 멤버그룹에서 대기중인지 확인 & 권한 확인
        MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
            //만약 현재인원이 최대인원보다 작다면
            if(yourMemberGroup.getGroup().getMemberCount() < yourMemberGroup.getGroup().getMaxMemberCount()) {
                yourMemberGroup.setAuthority(Authority.JOIN); //wait일 경우 join으로 바꿔줌
                yourMemberGroup.getGroup().plusMemberCount();

                //승인 전에 안넣어줬던 챗룸아이디를 멤버그룹에 넣어준 후
                yourMemberGroup.setChatroomId(myMemberGroup.getChatroomId());

                //조인이 되는 순간 채팅방도 가입시켜줘야 된다 => 챗멤버 생성필요
                Chatroom chatroom = chatRoomRepository.findById(myMemberGroup.getChatroomId()).orElseThrow(
                        () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 채팅방이 존재하지 않습니다."));

                ChatMember chatMember = ChatMember.createChatMember(yourMemberGroup.getMember(), chatroom);
                chatroom.getChatMembers().add(chatMember);

            }else{
                throw new CountNumberException(NUMBER_COUNT_ERROR_CODE, "그룹의 최대인원을 초과하였습니다.");
            }
        }

    }

    //그룹 참가자 거절
    @Transactional
    public void denyGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if(!Authority.OWNER.equals(myMemberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "거절을 할 수 없는 권한 입니다.");
        }

        MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그사람의 auth 확인 ->그사람의 권한이 wait일 경우
        if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
            memberGroupRepository.delete(yourMemberGroup);
            Group group= groupRepository.findById(groupId).orElseThrow(
                    () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
            group.getMemberGroups().remove(yourMemberGroup);
        }
    }



    //그룹 참가자 추방
    @Transactional
    public void banGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 추방할 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if(!Authority.OWNER.equals(myMemberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "거절을 할 수 없는 권한 입니다.");
        }

        //그사람도 같은 멤버그룹에 속했는지 확인
        MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그사람의 auth 확인 ->그사람의 권한이 join일 경우
        if(Authority.JOIN.equals(yourMemberGroup.getAuthority())){

            //1.채팅룸에서 드랍 => 1-1.챗멤버를 없애줘야함
            ChatMember chatMember = chatMemberRepository.findByMemberIdAndChatroomId(memberId, yourMemberGroup.getChatroomId())
                    .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 챗멤버가 존재하지 않습니다."));
            chatMemberRepository.delete(chatMember);
            //1-2. 채팅룸에서도 챗멤버를 없애 줘야함.
            Chatroom chatroom = chatRoomRepository.findById(yourMemberGroup.getChatroomId())
                    .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 채팅방이 존재하지 않습니다."));
            chatroom.getChatMembers().remove(chatMember);

            //2.멤버그룹 삭제
            memberGroupRepository.delete(yourMemberGroup);

            Group group= groupRepository.findById(groupId).orElseThrow(
                    () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
            group.getMemberGroups().remove(yourMemberGroup);
            yourMemberGroup.getGroup().minusMemberCount();
        }
    }

    //------------------------------------------------------------------------------------------------


    @Transactional
    public void setlocation(Long memberId, Long groupId, MemberGroupDto.Request request) {
        //해당하는 멤버그룹에 받아온 값을 넣어준다
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));
        if(Authority.WAIT.equals(memberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "가입 승인이 완료되지 않았습니다.");
        }
        memberGroup.setLocation(request.getStartLocationX(), request.getStartLocationY(), request.getStartAddress());
    }


    @Transactional
    public MemberGroupDto.Response recommendLocation(Long groupId) {
        //그룹에 속해있는 멤버들을 다 찾는다. => 멤버그룹에서 그룹아이디를 가진 멤버그룹을 다 찾음
        List<MemberGroup> memberGroupList = memberGroupRepository.findAllByGroupId(groupId);

        //멤버들의 locationx,y를 다 받아와서 평균값을 반환함
        Double totalX = 0.0;
        Double totalY = 0.0;
        for (MemberGroup memberGroup : memberGroupList){
            Double startLocationX = Double.parseDouble(memberGroup.getStartLocationX());
            Double startLocationY = Double.parseDouble(memberGroup.getStartLocationY());

            totalX += startLocationX;
            totalY += startLocationY;
        }

        String averageX = Double.toString(totalX / memberGroupList.size());
        String averageY = Double.toString(totalY / memberGroupList.size());

        MemberGroupDto.Response response = new MemberGroupDto.Response();
        response.setStartLocationX(averageX);
        response.setStartLocationY(averageY);

        return response;
    }

    public List<CrawlingDto.Response> getRecoCafe(MemberGroupDto.Response mdRes) {
        KakaoApiDto kakaoApiDto = kakaoWebClient.get()
                .uri(builder -> builder.path("/v2/local/search/keyword.json") //카카오 로컬- "키워드로 검색하기"
                        .queryParam("query", "스터디카페")
                        .queryParam("category_group_code", "CE7") //카테고리 그룹 코드
                        .queryParam("x", mdRes.getStartLocationX())
                        .queryParam("y", mdRes.getStartLocationY())
                        .queryParam("radius", "100") //반경
                        .queryParam("size", "3") //추천 카페 수
                        .build()
                )
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus != HttpStatus.OK,
                        clientResponse -> {
                            return clientResponse.createException()
                                    .flatMap(it -> Mono.error(new RuntimeException("WebClient 접근 예외. code : " + clientResponse.statusCode())));
                        })
                .bodyToMono(KakaoApiDto.class)
                .block();


        String title;
        String img;
        String star;

        WebDriver driver;
        WebElement element;

        List<CrawlingDto.Response> crawlingDtoList = new ArrayList<>();

        /*for (KakaoApiDto.Document document : kakaoApiDto.getDocuments()) {
            log.info(document.getPlace_url());
            String place_url = document.getPlace_url();

            // 드라이버 설치 경로
            String WEB_DRIVER_ID = "webdriver.chrome.driver";
            String WEB_DRIVER_PATH = "C:/Users/mj/Desktop/study/chromedriver.exe"; //폴더위치
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

            // WebDriver 옵션 설정
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-popup-blocking");       //팝업안띄움
            options.addArguments("headless");                       //브라우저 안띄움
            options.addArguments("--disable-gpu");            //gpu 비활성화
            options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음

            driver = new ChromeDriver(options);

            driver.get(place_url);
            Thread.sleep(7000); // 3. 페이지 로딩 대기 시간

            CrawlingDto.Response response = new CrawlingDto.Response();

            //카페이름
            if(!driver.findElements(By.xpath("//*[@id=\"mArticle\"]/div[1]/div[1]/div[2]/div/h2")).isEmpty()) {
                element = driver.findElement(By.xpath("//*[@id=\"mArticle\"]/div[1]/div[1]/div[2]/div/h2"));
                title = element.getText();
                log.info("-----------------------------" + title);
                response.setTitle(title);
            }else {
                title = null;
                log.info("------------title없음-----------------");
            }

            //평점
            if(!driver.findElements(By.xpath("//*[@id=\"mArticle\"]/div[5]/div[2]/div/em")).isEmpty()){
                element = driver.findElement(By.xpath("//*[@id=\"mArticle\"]/div[5]/div[2]/div/em"));
                star = element.getText();
                log.info("-----------------------------" + star);
                response.setStar(star);
            }else{
                star = null;
                log.info("-------------star없음----------------");
            }


            //이미지
            if(!driver.findElements(By.className("bg_present")).isEmpty()) {
                element = driver.findElement(By.className("bg_present"));
                String bgImage = element.getCssValue("background-image");
                img = bgImage.substring(5, bgImage.length() - 2);
                log.info("-----------------------------" + img);
                response.setImgUrl(img);
            }else{
                img = null;
                log.info("------------img없음-----------------");
            }

            crawlingDtoList.add(response);
          }*/
        log.info(crawlingDtoList.toString());
        return crawlingDtoList;
    }
}