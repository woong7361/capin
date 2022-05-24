package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.entity.Cafe;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.CafeDto;
import com.hanghae.finalp.entity.dto.CrawlingDto;
import com.hanghae.finalp.entity.dto.KakaoApiDto;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.CafeRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;



@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final WebClient kakaoWebClient;


    @Transactional
    public void selectCafe(Long memberId, CafeDto.Reqeust request, Long groupId){
        //카페 선택 버튼이 오너일 경우에만 보이는지 , 버튼은 누구나 볼 수 있고 오너만 누를 수 있게 할 것인지 정해야함
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupIdFetchGroup(memberId, groupId)
                .orElseThrow(() -> new MemberGroupNotExistException());
        if(!memberGroup.getAuthority().equals(Authority.OWNER)) {
            throw new AuthorOwnerException();
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
                .orElseThrow(() -> new MemberGroupNotExistException());
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorOwnerException();
        }
        cafeRepository.deleteByGroupId(groupId);
    }


    @Transactional
    public void setlocation(Long memberId, Long groupId, MemberGroupDto.Request request) {
        //해당하는 멤버그룹에 받아온 값을 넣어준다
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new MemberGroupNotExistException());
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

        for (KakaoApiDto.Document document : kakaoApiDto.getDocuments()) {
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
//            Thread.sleep(7000); // 3. 페이지 로딩 대기 시간

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
          }
        log.info(crawlingDtoList.toString());
        return crawlingDtoList;
    }
}
