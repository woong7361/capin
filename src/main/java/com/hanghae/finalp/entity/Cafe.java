package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cafe extends TimeStamped {

    @Id
//    @GeneratedValue
    @Column(name = "study_group_id")
    private Long id;
    private String locationName;
    private String locationX;
    private String locationY;
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "study_group_id")
    private Group group;


    //========================================생성자=============================================//

    private Cafe(String locationName, String locationX, String locationY, String address, Group group) {
        this.locationName = locationName;
        this.locationX = locationX;
        this.locationY = locationY;
        this.address = address;
        this.group = group;
    }

    //========================================생성 편의자=============================================//

    public static Cafe createCafe(String locationName, String locationX, String locationY, String address, Group group) {
        Cafe cafe = new Cafe(locationName, locationX, locationY, address, group);
        group.setGroupCafe(cafe);
        return cafe;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
