package com.hanghae.finalp.entity;

import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMessage {

    @Id
    @GeneratedValue
    @Column(name = "group_message_id")
    private Long id;

    private Long senderId;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_group_id")
    private MemberGroup memberGroup;


    //========================================생성자=============================================//

    private GroupMessage(Long senderId, String content, MessageType messageType) {
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
    }


    //========================================생성 편의자=============================================//

    private GroupMessage createGroupMessage(Long senderId, String content, MessageType messageType, MemberGroup memberGroup) {
        GroupMessage groupMessage = new GroupMessage(senderId, content, messageType);
        groupMessage.memberGroup = memberGroup;

        return groupMessage;
    }
}
