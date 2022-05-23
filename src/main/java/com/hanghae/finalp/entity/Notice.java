package com.hanghae.finalp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    private String message;
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    //========================================생성자=============================================//

    private Notice(String message, Member member) {
        this.message = message;
        this.isRead = false;
        this.member = member;
    }

    //========================================생성 편의자=============================================//

    public static Notice createGroupApplyNotice(String groupTitle, String targetUsername, Member ownerMember) {
        String message = targetUsername + "님이 " + groupTitle + "그룹에 가입신청을 했습니다.";
        return new Notice(message, ownerMember);
    }
    public static Notice createGroupApproveNotice(String groupTitle, Member member) {
        String message = member.getUsername() + "님의 " + groupTitle + "그룹 가입이 승인되었습니다.";
        return new Notice(message, member);
    }
    public static Notice createGroupDenyNotice(String groupTitle, Member member) {
        String message = member.getUsername() + "님의 " + groupTitle + "그룹 가입이 신청 취소되었습니다.";
        return new Notice(message, member);
    }
    public static Notice createGroupBanNotice(String groupTitle, Member member) {
        String message = member.getUsername() + "님이 " + groupTitle + "그룹에서 퇴장당하셨습니다.";
        return new Notice(message, member);
    }

    public void read() {
        this.isRead = true;
    }
}
