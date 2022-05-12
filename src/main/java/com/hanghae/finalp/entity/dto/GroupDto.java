package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.util.S3Utils;
import lombok.Data;

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
    public static class CreateReq {
        String groupTitle;
        String description;
        int maxMemberCount;
        String roughAddress;
    }
}
