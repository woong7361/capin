package com.hanghae.finalp.entity;

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
public class Member extends TimeStamped {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String kakaoId;
    private String username;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberGroup> memberGroups = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatMember> chatMembers = new ArrayList<>();



    //========================================생성자=============================================//


    private Member(String kakaoId, String username, String imageUrl) {
        this.kakaoId = kakaoId;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    //========================================생성 편의자=============================================//

    public static Member createMember(String kakaoId, String username, String imageUrl) {
        return new Member(kakaoId, username, imageUrl);
    }
    //========================================비즈니스 로직==============================================//
    public void patchUsername(String username) {
        this.username = username;
    }
    public void patchImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
