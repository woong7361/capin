package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.MemberGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;


public class GroupDto {

    @Data
    @NoArgsConstructor
    public static class SimpleRes {
        private Long groupId;
        private String imageUrl;
        private String groupTitle;
        private String roughAddress;
        private int memberCount;
        private int maxMemberCount;
        private String firstDay;
        private String lastDay;
        private List<MemberDto.ProfileRes> memberList; //이거이름 바꿔야됨ProfileRes 대신 다른거사용하기

        public SimpleRes(MemberGroup gm) {
            this.groupId = gm.getGroup().getId();
            this.imageUrl = gm.getGroup().getImageUrl();
            this.groupTitle = gm.getGroup().getGroupTitle();
            this.roughAddress = gm.getGroup().getRoughAddress();
            this.memberCount = gm.getGroup().getMemberCount();
            this.maxMemberCount = gm.getGroup().getMaxMemberCount();
            this.firstDay = gm.getGroup().getFirstDay();
            this.lastDay = gm.getGroup().getLastDay();
        }

        public SimpleRes(Group group) {
            this.groupId = group.getId();
            this.imageUrl = group.getImageUrl();
            this.groupTitle = group.getGroupTitle();
            this.roughAddress = group.getRoughAddress();
            this.memberCount = group.getMemberCount();
            this.maxMemberCount = group.getMaxMemberCount();
            this.firstDay = group.getFirstDay();
            this.lastDay = group.getLastDay();
        }

//        public SimpleRes(Group group, List<MemberDto.ProfileRes> memberList) {
//            this.groupId = group.getId();
//            this.imageUrl = group.getImageUrl();
//            this.groupTitle = group.getGroupTitle();
//            this.roughAddress = group.getRoughAddress();
//            this.memberCount = group.getMemberCount();
//            this.maxMemberCount = group.getMaxMemberCount();
//            this.firstDay = group.getFirstDay();
//            this.lastDay = group.getLastDay();
//            this.memberList = memberList;
//        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        @NotBlank(message = "그룹의 이름을 입력해 주세요.")
        String groupTitle;

        String description;

        @ColumnDefault("30")
        @Max(value = 100, message = "그룹의 최대 인원수는 100명 입니다.")
        @Min(value = 2, message = "그룹의 최소 인원수는 2명 입니다.")
        int maxMemberCount;

        String roughAddress;

        @NotBlank(message = "YYYY.MM.DD 형식에 맞게 입력해 주세요.")
        @Pattern(regexp = "^([12]\\d{3}.(0[1-9]|1[0-2]).(0[1-9]|[12]\\d|3[01]))$")
        String firstDay;

        @NotBlank(message = "YYYY.MM.DD 형식에 맞게 입력해 주세요.")
        @Pattern(regexp = "^([12]\\d{3}.(0[1-9]|1[0-2]).(0[1-9]|[12]\\d|3[01]))$")
        String lastDay;
    }
}
