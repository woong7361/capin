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
public class Board extends TimeStamped {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String content;
    private Long loveCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Love> loves = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    //========================================생성자=============================================//
    private Board(String title, String content, Member member, Color color) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.color = color;
    }

    //========================================생성 편의자=============================================//
    public static Board createBoard(String title, String content, Member member, Color color, List<String> urls) {
        Board board = new Board(title, content, member, color);

        //null check 필요?
        urls.forEach((url) -> board.getImages().add(Image.createImage(url, board)));
        board.getMember().getBoards().add(board);

        return board;
    }


}
