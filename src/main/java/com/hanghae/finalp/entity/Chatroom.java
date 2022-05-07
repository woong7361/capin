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

    @OneToMany(mappedBy = "chatroom")
    private List<ChatMember> chatMembers = new ArrayList<>();

    @OneToMany(mappedBy = "chatroom")
    private List<MemberGroup> memberGroups = new ArrayList<>();

}