package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DmMessage {

    @Id @GeneratedValue
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


    //========================================생성 편의자=============================================//
}