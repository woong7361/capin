package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group")
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "study_group_id")
    private Long id;

    private String groupTitle;
    private String description;
    private int memberCount;
    private int maxMemberCount;
    private String roughAddress;
    private String imageUrl;

    private String imageFullUrl;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<MemberGroup> memberGroups = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    private Cafe cafe;


    //========================================생성자=============================================//

    private Group(String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl, String imageFullUrl) {
        this.groupTitle = groupTitle;
        this.description = description;
        this.memberCount = 1;
        this.maxMemberCount = maxMemberCount;
        this.roughAddress = roughAddress;
        this.imageUrl = imageUrl;
        this.imageFullUrl = imageFullUrl;
    }

    //========================================생성 편의자=============================================//

    public static Group createGroup(
            String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl, String imageFullUrl, Member member) {
        Group group = new Group(groupTitle, description, maxMemberCount, roughAddress, imageUrl, imageFullUrl); //group을 만들고
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group); //memberGroup을 만들고
        group.getMemberGroups().add(memberGroup); //group안에 memberGroup을 넣어준다
        Chatroom.createChatroomByGroup(groupTitle, memberGroup); //group을 만들때 chatroom도 만들어줘야 하므로

        return group;
    }

    //========================================비즈니스 로직=============================================//
    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

}


