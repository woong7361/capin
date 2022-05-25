package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGroup extends TimeStamped {

    @Id
    @GeneratedValue
    @Column(name = "member_group_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Authority authority;
    private String startLocationX;
    private String startLocationY;
    private String startAddress;
    private Long chatroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private Group group;

    //========================================생성자=============================================//

    private MemberGroup(Authority authority, Member member, Group group, Long chatroomId) {
        this.authority = authority;
        this.member = member;
        this.group = group;
        this.chatroomId = chatroomId;
    }

    private MemberGroup(Authority authority, Long memberId, Long groupId, Long chatroomId) {
        this.authority = authority;
        this.member = Member.createMappingMember(memberId);
        this.group = Group.createMappingGroup(groupId);
        this.chatroomId = chatroomId;
    }

    //========================================생성 편의자=============================================//

    public static MemberGroup createMemberGroup(Authority authority, Member member, Group group, Long chatroomId) {
        return new MemberGroup(authority, member, group, chatroomId);
    }

    public static MemberGroup createMemberGroup(Authority authority, Long memberId, Long groupId, Long chatroomId) {
        return new MemberGroup(authority, memberId, groupId, chatroomId);
    }

    public void joinGroup() {
        this.authority = Authority.JOIN;
    }

    public void joinGroupChatRoom(Long chatroomId) { this.chatroomId = chatroomId; }

    public void setStartLocation(String startLocationX, String startLocationY, String startAddress){
        this.startLocationX = startLocationX;
        this.startLocationY = startLocationY;
        this.startAddress = startAddress;
    }

}
