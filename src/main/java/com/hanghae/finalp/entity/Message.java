package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.entity.mappedsuperclass.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends TimeStamped {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    private Long senderId;
    private String senderName;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_member_id")
//    private ChatMember chatMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;
    //========================================생성자=============================================//

    private Message(Long senderId, String content, MessageType messageType, Chatroom chatroom, String senderName) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.messageType = messageType;
        this.chatroom = chatroom;
    }


    //========================================생성 편의자=============================================//

    public static Message createMessage(Long senderId, String senderName, String content, MessageType messageType, Chatroom chatroom) {
        Message message = new Message(senderId, content, messageType, chatroom, senderName);

        return message;
    }


}