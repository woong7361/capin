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
        Member memberA = Member.createMember("Kakao_A", "userA",
                "https://d1ai09q40aghzs.cloudfront.net/노래부르는사진.jpg-20222928012949");
        Member memberB = Member.createMember("Kakao_B", "userB",
                "https://d1ai09q40aghzs.cloudfront.net/images.png-20223428013401");
        Member memberC = Member.createMember("Kakao_C", "userC", null);
        Member memberD = Member.createMember("Kakao_D", "userD", null);
        Member memberE = Member.createMember("Kakao_E", "userE", null);
        Member memberF = Member.createMember("Kakao_F", "userF", null);
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

        Group groupA = Group.createGroup("group1", "group1 desc", 5, "서초/신사/방배",
                "https://d1ai09q40aghzs.cloudfront.net/hub.png-20223028013039",
                memberA, 9999L, "2022.06.10", "2022.07.22");
        Group groupB = Group.createGroup("group2", "group2 desc", 4, "천호/길동/둔촌",
                "https://d1ai09q40aghzs.cloudfront.net/twt.jfif-20223428013459",
                memberA, 99999L, "2022.06.18", "2022.07.25");
        Group groupC = Group.createGroup("group3", "group3 desc", 7, "서초/신사/방배",
                null, memberB, 999999L, "2022.11.21", "2023.04.12");
        Group groupD = Group.createGroup("group4", "group4 desc", 3, "강북/수유/미아",
                null, memberC, 9999999L, "2022.08.19", "2022.09.16");
        groupRepository.save(groupA);
        groupRepository.save(groupB);
        groupRepository.save(groupC);
        groupRepository.save(groupD);

        MemberGroup memberGroupA = MemberGroup.createMemberGroup(Authority.JOIN, memberD, groupA, 9999L);
        MemberGroup memberGroupB = MemberGroup.createMemberGroup(Authority.JOIN, memberE, groupA, 9999L);
        MemberGroup memberGroupC = MemberGroup.createMemberGroup(Authority.WAIT, memberF, groupA, 9999L);
        MemberGroup memberGroupX = MemberGroup.createMemberGroup(Authority.WAIT, memberB, groupA, 9999L);
        MemberGroup memberGroupY = MemberGroup.createMemberGroup(Authority.WAIT, memberC, groupA, 9999L);
        for (int i = 0; i <2; i++) groupA.plusMemberCount();
        groupRepository.save(groupA);
        MemberGroup memberGroupD = MemberGroup.createMemberGroup(Authority.JOIN, memberB, groupB, 99999L);
        MemberGroup memberGroupE = MemberGroup.createMemberGroup(Authority.JOIN, memberC, groupB, 99999L);
        for (int i = 0; i <2; i++) groupB.plusMemberCount();
        groupRepository.save(groupB);
        MemberGroup memberGroupF = MemberGroup.createMemberGroup(Authority.WAIT, memberD, groupB, 99999L);
        MemberGroup memberGroupG = MemberGroup.createMemberGroup(Authority.WAIT, memberA, groupC, 999999L);
        MemberGroup memberGroupH = MemberGroup.createMemberGroup(Authority.WAIT, memberE, groupC, 999999L);
        MemberGroup memberGroupI = MemberGroup.createMemberGroup(Authority.WAIT, memberF, groupC, 999999L);

        memberGroupA.setStartLocation("127.36283102249932", "37.514322572335935", "어딜까요?");
        memberGroupB.setStartLocation("127.46283102249932", "37.414322572335935", "어딜까요?");
        memberGroupC.setStartLocation("127.56283102249932", "37.314322572335935", "어딜까요?");

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
