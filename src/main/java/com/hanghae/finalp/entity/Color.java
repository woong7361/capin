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

    @Id
    @GeneratedValue
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


    //========================================생성자=============================================//

    private Color(int colorR, int colorG, int colorB, DayNight dayNight, Member member) {
        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
        this.dayNight = dayNight;
        this.member = member;
    }

    //========================================생성 편의자=============================================//
    public static Color createColor(int colorR, int colorG, int colorB, DayNight dayNight, Member member) {
        Color color = new Color(colorR, colorG, colorB, dayNight, member);
        color.getMember().getColors().add(color);
        return color;
    }

}
