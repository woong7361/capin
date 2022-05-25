package com.hanghae.finalp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMember {

    @Id @GeneratedValue
    @Column(name = "chat_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    //========================================생성자=============================================//

    private ChatMember(Member member, Chatroom chatroom) {
        this.member = member;
        this.chatroom = chatroom;
    }

    //========================================생성 편의자=============================================//

    public static ChatMember createChatMember(Member member, Chatroom chatroom) {
        return new ChatMember(member, chatroom);
    }


}