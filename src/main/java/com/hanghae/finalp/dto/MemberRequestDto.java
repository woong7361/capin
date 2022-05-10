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


/*    public Member toEntity(){
        Member build = Member.builder()
                .username(username)
                .imageUrl(imageUrl)
                .build();
        return build;
    }*/

    // 이거 좀더 알아보기
//    @Builder
//    public MemberRequestDto(String username, String imageUrl) {
//        this.username = username;
//        this.imageUrl = imageUrl;
//    }
}
