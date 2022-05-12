package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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


    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<MemberGroup> memberGroups = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    private Cafe cafe;


    //========================================생성자=============================================//

    private Group(String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl) {
        this.groupTitle = groupTitle;
        this.description = description;
        this.memberCount = 1;
        this.maxMemberCount = maxMemberCount;
        this.roughAddress = roughAddress;
        this.imageUrl = imageUrl;
    }

    //========================================생성 편의자=============================================//

    public static Group createGroup(
            String groupTitle, String description, int maxMemberCount, String roughAddress, String imageUrl, Member member) {
        Group group = new Group(groupTitle, description, maxMemberCount, roughAddress, imageUrl); //group을 만들고
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group); //memberGroup을 만들고
        group.getMemberGroups().add(memberGroup); //group안에 memberGroup을 넣어준다
        Chatroom.createChatroomByGroup(groupTitle, memberGroup); //group을 만들때 chatroom도 만들어줘야 하므로
        return group;
    }

    public static Group createGroup(GroupDto.CreateReq createReq, String imageUrl, Member member) {
        Group group = new Group(createReq.getGroupTitle(), createReq.getDescription(), createReq.getMaxMemberCount(),
                createReq.getRoughAddress(), imageUrl);
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group); //memberGroup을 만들고
        group.getMemberGroups().add(memberGroup); //group안에 memberGroup을 넣어준다
        Chatroom.createChatroomByGroup(createReq.getGroupTitle(), memberGroup); //group을 만들때 chatroom도 만들어줘야 하므로
        return group;
    }


    //========================================비즈니스 로직=============================================//
    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public void patch(GroupDto.CreateReq createReq, String imageUrl) {
        this.groupTitle = createReq.getGroupTitle();
        this.description = createReq.getDescription();
        this.maxMemberCount = createReq.getMaxMemberCount();
        this.roughAddress = createReq.getRoughAddress();
        this.imageUrl = imageUrl;
    }
}


