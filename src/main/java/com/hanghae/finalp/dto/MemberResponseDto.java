package com.hanghae.finalp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberResponseDto {
    private String username;
    private String imageFullUrl;

/*    public Member toEntity(){
        Member build = Member.builder()
                .username(username)
                .imageFullUrl(imageFullUrl)
                .build();
        return build;
    }*/


    public MemberResponseDto(String username, String imageFullUrl) {
        this.username = username;
        this.imageFullUrl = imageFullUrl;
    }

/*    public MemberResponseDto(Member member){
        this.username = member.getUsername();
        this.imageFullUrl = member.getImageFullUrl();
    }*/
}
