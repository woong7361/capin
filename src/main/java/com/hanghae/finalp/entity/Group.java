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
        Group group = new Group(groupTitle, description, maxMemberCount, roughAddress, imageUrl);
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.OWNER, member, group);
        group.getMemberGroups().add(memberGroup);

        return group;

    }

    //========================================비즈니스 로직=============================================//
    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

}


