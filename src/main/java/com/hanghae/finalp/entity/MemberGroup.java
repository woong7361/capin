package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGroup {

    @Id
    @GeneratedValue
    @Column(name = "member_group_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String startLocationX;
    private String startLocationY;
    private String startAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

//    @OneToMany(mappedBy = "memberGroup", cascade = CascadeType.ALL)
//    private List<GroupMessage> groupMessages;


    //========================================생성자=============================================//

    private MemberGroup(Authority authority, Member member, Group group) {
        this.authority = authority;
        this.member = member;
        this.group = group;
    }


    //========================================생성 편의자=============================================//

    public static MemberGroup createMemberGroup(Authority authority, Member member, Group group) {
        return new MemberGroup(authority, member, group);
    }


    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }
}
