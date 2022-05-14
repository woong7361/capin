package com.hanghae.finalp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberResponseDto {
    private String username;
    private String imageUrl;


    public MemberResponseDto(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }

}
