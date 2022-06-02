package com.hanghae.finalp.mockdata;

import com.hanghae.finalp.entity.*;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import com.hanghae.finalp.service.*;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class MockData {

    private final GroupService groupService;
    private final CafeService cafeService;
    private final MemberGroupService memberGroupService;
    private final NoticeService noticeService;
    private final S3Service s3Service;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtils redisUtils;

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final CafeRepository cafeRepository;
    private final NoticeRepository noticeRepository;


    @PostConstruct
    @Transactional
    public void init() {
        Member memberA = Member.createMember("Kakao_A", "이기영",
                "https://d1ai09q40aghzs.cloudfront.net/노래부르는사진.jpg-20222928012949");
        Member memberB = Member.createMember("Kakao_B", "박기린",
                "https://d1ai09q40aghzs.cloudfront.net/images.png-20223428013401");
        Member memberC = Member.createMember("Kakao_C", "신승훈",
                "https://dck3y7rbupkt9.cloudfront.net/static/cat-g19ed816b3_1920.jpg");
        Member memberD = Member.createMember("Kakao_D", "박하이",
                "https://dck3y7rbupkt9.cloudfront.net/static/cat-gdad734830_1920.jpg");
        Member memberE = Member.createMember("Kakao_E", "시라나",
                "https://dck3y7rbupkt9.cloudfront.net/static/cat-gf9b524d2f_1920.png");
        Member memberF = Member.createMember("Kakao_F", "신시나",
                "https://dck3y7rbupkt9.cloudfront.net/static/dog-gc596146c9_1920.jpg");
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);
        memberRepository.save(memberE);
        memberRepository.save(memberF);

        String refreshTokenA = jwtTokenUtils.createRefreshToken(memberA.getId());
        redisUtils.setRefreshTokenDataExpire(memberA.getId().toString(), refreshTokenA,
                jwtTokenUtils.getRefreshTokenExpireTime(refreshTokenA));
        String refreshTokenB = jwtTokenUtils.createRefreshToken(memberB.getId());
        redisUtils.setRefreshTokenDataExpire(memberB.getId().toString(), refreshTokenB,
                jwtTokenUtils.getRefreshTokenExpireTime(refreshTokenB));
        String refreshTokenC = jwtTokenUtils.createRefreshToken(memberC.getId());
        redisUtils.setRefreshTokenDataExpire(memberC.getId().toString(), refreshTokenC,
                jwtTokenUtils.getRefreshTokenExpireTime(refreshTokenC));

        Chatroom groupChatroom1 = Chatroom.createChatroomByGroup("group1", memberA);
        Chatroom groupChatroom2 = Chatroom.createChatroomByGroup("group2", memberA);
        Chatroom groupChatroom3 = Chatroom.createChatroomByGroup("group3", memberB);
        Chatroom groupChatroom4 = Chatroom.createChatroomByGroup("group4", memberC);
        Chatroom groupChatroom5 = Chatroom.createChatroomByGroup("group5", memberC);
        Chatroom groupChatroom6 = Chatroom.createChatroomByGroup("group6", memberC);
        Chatroom groupChatroom7 = Chatroom.createChatroomByGroup("group7", memberC);
        Chatroom groupChatroom8 = Chatroom.createChatroomByGroup("group8", memberC);
        Chatroom groupChatroom9 = Chatroom.createChatroomByGroup("group9", memberC);
        chatRoomRepository.save(groupChatroom1);
        chatRoomRepository.save(groupChatroom2);
        chatRoomRepository.save(groupChatroom3);
        chatRoomRepository.save(groupChatroom4);
        chatRoomRepository.save(groupChatroom5);
        chatRoomRepository.save(groupChatroom6);
        chatRoomRepository.save(groupChatroom7);
        chatRoomRepository.save(groupChatroom8);
        chatRoomRepository.save(groupChatroom9);

        Group groupA = Group.createGroup("서초동 영어 스터디", "안녕하세요. ㅎㅎ \n" +
                        "혼자서 공부하면 의지가 약해지는 분들 같이 공부해요!\n" +
                        "목표를 정해서 각자 풀어오고 어떻게 풀었는지 소개하는 방식으로 진행하고 싶습니다. ", 5, "서초/신사/방배",
                "https://d1ai09q40aghzs.cloudfront.net/hub.png-20223028013039",
                memberA, groupChatroom1.getId(), "2022.06.10", "2022.07.22");
        Group groupB = Group.createGroup("훈의 프로그래밍 스터디", "안녕하세요~\n" +
                        "저희 스터디 모임에서 스터디원을 모집하려고 합니다.\n" +
                        "이번에 진행하게 될 스터디는 블로그 포스팅, 코틀린 인 액션, 도커&쿠버네티스 총 3개 입니다.", 4, "천호/길동/둔촌",
                "https://d1ai09q40aghzs.cloudfront.net/twt.jfif-20223428013459",
                memberA, groupChatroom2.getId(), "2022.06.18", "2022.07.25");
        Group groupC = Group.createGroup("취준 면접 대비 스터디", "안녕하세요 8월 목표로 퇴사하고 이직 준비하고 있는 사람입니다!\n" +
                        "혼자 하려니까 미루기도 하고 이러다가 목표한 날짜보다 더 늦게 취직을 하거나 목표한 회사보다 너무 낮은 회사를 갈 것 같아.. 마음은 급해지는데 몸이 안 따라와서 비슷한 목표를 가진 분들과 같이 하고 싶어 글 올리게 되었습니다.\n" +
                        "자유롭게 하는 것도 좋지만 약간의 강제성을 부여하기 위해 \n" +
                        "지각 1000원, 결석 2000원의 벌금제도를 운영해서 해보려고 합니다. ", 7, "서초/신사/방배",
                "https://dck3y7rbupkt9.cloudfront.net/static/kitten-g93fa00fdc_1920.jpg",
                memberB, groupChatroom3.getId(), "2022.11.21", "2023.04.19");
        Group groupD = Group.createGroup("열정 스터디", "안녕하세요! \n" +
                        "웹개발 경력 1년차 신입개발자인데 최근 퇴사하고 이직 준비하고 있습니다.\n" +
                        "혼자 하려니까 좀 게을러질때도 있어서 스터디 만들어서 같이 응원하면서 하면 좋지 않을까 싶어 스터디를 만들었습니다.\n" +
                        "리액트 쪽 주로 공부하고 있습니다. \n" +
                        "개발 이야기도 같이 나눠요! ", 3, "강북/수유/미아",
                "https://dck3y7rbupkt9.cloudfront.net/static/kittens-ge319496a9_1920.jpg",
                memberC, groupChatroom4.getId(), "2022.08.19", "2022.09.13");
        Group groupE = Group.createGroup("신사역 근처 안드로이드 스터디", "안녕하세요, 송파구 삼전동에 사는 쪼랩 안드로이드 개발자입니다.\n" +
                        "다름이 아니라, 제가 사는 곳(잠실, 송파)주변에서 \"하, 방구석에서 혼자 공부 할려니 귀찮다..\"라고 생각하시는 분들중에\n" +
                        "안드로이드, 코틀린 스터디를 같이 하실분이 있다면 같이 했으면 좋겠어서 이렇게 글을 작성합니다.", 4, "서초/신사/방배",
                "https://dck3y7rbupkt9.cloudfront.net/static/maltese-gde7f16253_1920.jpg",
                memberC, groupChatroom5.getId(), "2022.07.30", "2022.09.27");
        Group groupF = Group.createGroup("슬로우 먼데이", "취준생인데 혼자 공부하는것보다 여럿이서 규칙을 지키면서 공부하는게 더 도움이 돼서 만들었습니다. 서로 화면 공유해서 같이 공부하는 느낌을 받고 싶은게 목적입니다. \n" +
                        "모각코는 게더타운으로 진행됩니다. ", 4, "서초/신사/방배",
                "https://dck3y7rbupkt9.cloudfront.net/static/hot-air-balloon-g7cfe52200_1280.jpg",
                memberC, groupChatroom6.getId(), "2022.06.15", "2022.09.16");
        Group groupG = Group.createGroup("자율 스터디", "안녕하세요.\n" +
                        "아, 퇴근하고 공부해야하는데.\n" +
                        "아, 공부 말고도 다른 거 하고 싶은데, 짬이 안나네.\n" +
                        "하는 경우 반드시 있지 않았나요? 그런데 혼자 하기엔 쓸쓸해서 집중도 안되고요.", 6, "강북/수유/미아",
                "https://dck3y7rbupkt9.cloudfront.net/static/iceland-gd8b5bdc7f_1920.jpg",
                memberC, groupChatroom7.getId(), "2022.07.20", "2022.11.14");
        Group groupH = Group.createGroup("알고리즘 대비 스터", "스터디 : 매주 토요일 10시 ~ 12시\n" +
                        "1주 잠실 이룸 스터디 - 1주 게더타운을 이용한 온라인 스터디\n" +
                        "격주로 온오프 섞어서 진행해요", 5, "강북/수유/미아",
                "https://dck3y7rbupkt9.cloudfront.net/static/tree-g89fcd4ba9_1280.jpg",
                memberC, groupChatroom8.getId(), "2022.08.24", "2022.09.16");
        Group groupI = Group.createGroup("사이드 프로젝트 스터디", "안녕하세요. 채팅형 메모 앱 ‘Memochat’ 팀입니다.\n" +
                        "기획/디자인 단계를 마쳐 디자인 시스템&컴포넌트 정리까지 끝난 사이드 프로젝트 개발자를 구합니다.", 2, "강북/수유/미아",
                "https://dck3y7rbupkt9.cloudfront.net/static/cat-gf9b524d2f_1920.png",
                memberC, groupChatroom9.getId(), "2022.06.11", "2022.11.23");
        groupRepository.save(groupA);
        groupRepository.save(groupB);
        groupRepository.save(groupC);
        for (int i = 0; i <1; i++) groupD.plusMemberCount();
        groupRepository.save(groupD);
        for (int i = 0; i <3; i++) groupE.plusMemberCount();
        groupRepository.save(groupE);
        for (int i = 0; i <2; i++) groupF.plusMemberCount();
        groupRepository.save(groupF);
        for (int i = 0; i <1; i++) groupG.plusMemberCount();
        groupRepository.save(groupG);
        for (int i = 0; i <3; i++) groupH.plusMemberCount();
        groupRepository.save(groupH);
        for (int i = 0; i <1; i++) groupI.plusMemberCount();
        groupRepository.save(groupI);

        MemberGroup memberGroupA = MemberGroup.createMemberGroup(Authority.JOIN, memberD, groupA, groupChatroom1.getId());
        MemberGroup memberGroupB = MemberGroup.createMemberGroup(Authority.JOIN, memberE, groupA, groupChatroom1.getId());
        MemberGroup memberGroupC = MemberGroup.createMemberGroup(Authority.WAIT, memberF, groupA, groupChatroom1.getId());
        MemberGroup memberGroupX = MemberGroup.createMemberGroup(Authority.WAIT, memberB, groupA, groupChatroom1.getId());
        MemberGroup memberGroupY = MemberGroup.createMemberGroup(Authority.WAIT, memberC, groupA, groupChatroom1.getId());
        for (int i = 0; i <2; i++) groupA.plusMemberCount();
        groupRepository.save(groupA);
        MemberGroup memberGroupD = MemberGroup.createMemberGroup(Authority.JOIN, memberB, groupB, groupChatroom2.getId());
        MemberGroup memberGroupE = MemberGroup.createMemberGroup(Authority.JOIN, memberC, groupB, groupChatroom2.getId());
        MemberGroup memberGroupF = MemberGroup.createMemberGroup(Authority.WAIT, memberD, groupB, groupChatroom2.getId());
        for (int i = 0; i <2; i++) groupB.plusMemberCount();
        groupRepository.save(groupB);
        MemberGroup memberGroupG = MemberGroup.createMemberGroup(Authority.WAIT, memberA, groupC, groupChatroom3.getId());
        MemberGroup memberGroupH = MemberGroup.createMemberGroup(Authority.WAIT, memberE, groupC, groupChatroom3.getId());
        MemberGroup memberGroupI = MemberGroup.createMemberGroup(Authority.WAIT, memberF, groupC, groupChatroom3.getId());

        memberGroupA.setStartLocation("127.02476252269932", "37.465965522735935", "서울 강남구 개포로28길 4");
        memberGroupB.setStartLocation("127.06376252349932", "37.495865532335935", "서울 강남구 남부순환로378길 18");
//        memberGroupC.setStartLocation("127.06476257249932", "37.495865522335035", "서울시 사당동");

        memberGroupRepository.save(memberGroupA);
        memberGroupRepository.save(memberGroupB);
        memberGroupRepository.save(memberGroupC);
        memberGroupRepository.save(memberGroupD);
        memberGroupRepository.save(memberGroupE);
        memberGroupRepository.save(memberGroupF);
        memberGroupRepository.save(memberGroupG);
        memberGroupRepository.save(memberGroupH);
        memberGroupRepository.save(memberGroupI);
        memberGroupRepository.save(memberGroupX);
        memberGroupRepository.save(memberGroupY);


        Cafe cafe = Cafe.createCafe("열공카페", "127.26883192249932", "37.454322572335935",
                "영등포구 마포대로 3길 22번지", groupA);
        cafeRepository.save(cafe);

        Notice noticeA = Notice.createGroupApplyNotice(groupA.getGroupTitle(), memberF.getUsername(), memberA);
        Notice noticeB = Notice.createGroupApplyNotice(groupA.getGroupTitle(), memberB.getUsername(), memberA);
        Notice noticeC = Notice.createGroupApplyNotice(groupA.getGroupTitle(), memberC.getUsername(), memberA);

        noticeRepository.save(noticeA);
        noticeRepository.save(noticeB);
        noticeRepository.save(noticeC);


    }


}
