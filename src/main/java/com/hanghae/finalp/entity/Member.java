package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends TimeStamped {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String kakaoUsername;
    private String kakaoId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Board> boards;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Love> loves;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Color> colors;

}
