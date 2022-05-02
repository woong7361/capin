package com.hanghae.finalp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }


    //========================================생성자=============================================//

    private Image(String imageUrl, Board board) {
        this.imageUrl = imageUrl;
        this.board = board;
    }


    //========================================생성 편의자=============================================//
    public static Image createImage(String imageUrl, Board board) {
        return new Image(imageUrl, board);
    }
}
