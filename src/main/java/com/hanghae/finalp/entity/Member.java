package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    private Member(String kakaoId, String username, String imageUrl) {
        //여기를 MemberRequestDto memberRequestDto로 바꿔
        this.kakaoId = kakaoId;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    //========================================생성 편의자=============================================//

    public static Member createMember(String kakaoId, String username, String imageUrl) {
        return new Member(kakaoId, username, imageUrl);
    }

    //========================================비즈니스 로직==============================================//
    public void patchMember(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public void setImageUrl(String fileName) {
        this.imageUrl = fileName;
    }
}
