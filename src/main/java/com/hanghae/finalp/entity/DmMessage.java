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
public class DmMessage extends TimeStamped {

    @Id
    @GeneratedValue
    @Column(name = "dm_message_id")
    private Long id;

    private Long senderId;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne
    @JoinColumn(name = "chat_member_id")
    private ChatMember chatMember;


    //========================================생성자=============================================//

    private DmMessage(Long senderId, String content, MessageType messageType) {
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
    }


    //========================================생성 편의자=============================================//

    private DmMessage createDmMessage(Long senderId, String content, MessageType messageType, ChatMember chatMember) {
        DmMessage dmMessage = new DmMessage(senderId, content, messageType);
        dmMessage.chatMember = chatMember;

        return dmMessage;
    }


}