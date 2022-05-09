package com.hanghae.finalp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom {

    @Id
    @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    private String chatroomTitle;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL)
    private List<ChatMember> chatMembers = new ArrayList<>();

//    @OneToMany(mappedBy = "chatroom")
//    private List<MemberGroup> memberGroups = new ArrayList<>();

    //========================================생성자=============================================//
    private Chatroom(String chatroomTitle) {
        this.chatroomTitle = chatroomTitle;
    }

    //========================================생성 편의자=============================================//
    public static Chatroom createChatroomByGroup(String chatroomTitle, MemberGroup memberGroup) {
        Chatroom chatroom = new Chatroom(chatroomTitle);
        memberGroup.setChatroom(chatroom);
        return chatroom;
    }

    public static Chatroom createChatroomByMember(String chatroomTitle, Member member1, Member member2) {
        Chatroom chatroom = new Chatroom(chatroomTitle);

        ChatMember chatMember1 = ChatMember.createChatMember(member1, chatroom);
        ChatMember chatMember2 = ChatMember.createChatMember(member2, chatroom);
        chatroom.getChatMembers().add(chatMember1);
        chatroom.getChatMembers().add(chatMember2);

        return chatroom;
    }
}