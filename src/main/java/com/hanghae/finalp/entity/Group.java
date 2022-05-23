package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
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
public class Group extends TimeStamped {

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
    private String firstDay;
    private String lastDay;


    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<MemberGroup> memberGroups = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.ALL)
    private Cafe cafe;


    //========================================생성자=============================================//

    private Group(String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl, String firstDay, String lastDay) {
        this.groupTitle = groupTitle;
        this.description = description;
        this.memberCount = 1;
        this.maxMemberCount = maxMemberCount;
        this.roughAddress = roughAddress;
        this.imageUrl = imageUrl;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    //========================================생성 편의자=============================================//

    public static Group createGroup(
            String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl, Member member, Long chatroomId, String firstDay, String lastDay) {
        Group group = new Group(groupTitle, description, maxMemberCount, roughAddress, imageUrl, firstDay, lastDay); //group을 만들고
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group, chatroomId); //memberGroup을 만들고
        group.getMemberGroups().add(memberGroup); //group안에 memberGroup을 넣어준다
        return group;
    }

    public static Group createGroup(GroupDto.CreateReq createReq, String imageUrl, Member member, Long chatroomId) {
        Group group = new Group(createReq.getGroupTitle(), createReq.getDescription(), createReq.getMaxMemberCount(),
                createReq.getRoughAddress(), imageUrl, createReq.getFirstDay(), createReq.getLastDay());
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group, chatroomId); //memberGroup을 만들고
        group.getMemberGroups().add(memberGroup); //group안에 memberGroup을 넣어준다
        return group;
    }


    //========================================비즈니스 로직=============================================//
    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void patch(GroupDto.CreateReq createReq, String imageUrl) {
        this.groupTitle = createReq.getGroupTitle();
        this.description = createReq.getDescription();
        this.maxMemberCount = createReq.getMaxMemberCount();
        this.roughAddress = createReq.getRoughAddress();
        this.lastDay = createReq.getLastDay();
        this.imageUrl = imageUrl;
    }

    public void plusMemberCount(){
        this.memberCount += 1;
    }
    public void minusMemberCount(){
        this.memberCount -= 1;
    }

}


