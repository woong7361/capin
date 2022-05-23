package com.hanghae.finalp.config.exception.code;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    MAX_MEMBER_EXCEPTION(400, "error.count", "max Member Exception, can not add member"),
    DUPLICATE_REQUSET(400, "error.duplicate" , "duplicate request error"),
//    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
//    METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
//    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),

    // Database,
    ENTITY_NOT_EXIST(400, "error.entity.notExist", "entity not found exception"),
    CAFE_NOT_EXIST(400,"error.entity.notExist.cafe" , "cafe entity not found exception"),
    MEMBER_NOT_EXIST(400,"error.entity.notExist.member" , "member entity not found exception"),
    GROUP_NOT_EXIST(400,"error.entity.notExist.group" , "group entity not found exception"),
    MEMBER_GROUP_NOT_EXIST(400,"error.entity.notExist.membergroup" , "memberGroup entity not found exception"),

    // Token,
    TOKEN_EXCEPTION(400, "error.token", "token exception"),
    REFRESH_TOKEN_EXCEPTION(400, "error.token.refresh", "refresh token exception"),

    // Upload/Download,
    S3_EXCEPTION(400, "error.s3", "s3 upload/download exception"),

    // Authority
    AUTHORITY_EXCEPTION(403, "error.Authority", "authority error"),
    AUTHORITY_OWNER_EXCEPTION(403, "error.authority.owner", "authority error: required owner Authority"),
    AUTHORITY_WAIT_EXCEPTION(403, "error.authority.wait", "authority error: required wait Authority"),
    AUTHORITY_JOIN_EXCEPTION(403, "error.authority.join", "authority error: required join Authority"),;

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }


}