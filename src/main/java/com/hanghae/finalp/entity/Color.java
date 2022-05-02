package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Color extends TimeStamped {

    @Id @GeneratedValue
    @Column(name = "color_id")
    private Long id;

    private int colorR;
    private int colorG;
    private int colorB;

    @Enumerated(EnumType.STRING)
    private DayNight dayNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
