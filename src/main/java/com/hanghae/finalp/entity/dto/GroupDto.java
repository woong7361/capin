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

public class GroupDto {

    @Data
    public static class SimpleRes {
        private Long groupId;
        private String imageUrl;
        private String groupTitle;
        private String roughAddress;
        private int memberCount;
        private int maxMemberCount;

        public SimpleRes(MemberGroup gm) {
            this.groupId = gm.getGroup().getId();
            this.imageUrl = gm.getGroup().getImageUrl();
            this.groupTitle = gm.getGroup().getGroupTitle();
            this.roughAddress = gm.getGroup().getRoughAddress();
            this.memberCount = gm.getGroup().getMemberCount();
            this.maxMemberCount = gm.getGroup().getMaxMemberCount();
        }
        public SimpleRes(Group group) {
            this.groupId = group.getId();
            this.imageUrl = group.getImageUrl();
            this.groupTitle = group.getGroupTitle();
            this.roughAddress = group.getRoughAddress();
            this.memberCount = group.getMemberCount();
            this.maxMemberCount = group.getMaxMemberCount();
        }
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
    }
}
