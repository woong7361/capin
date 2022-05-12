package com.hanghae.finalp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@NoArgsConstructor
public class MemberRequestDto { //유저 생성시에 쓰인다
    private String username;
    private String imageUrl;

}
