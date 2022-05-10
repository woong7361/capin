package com.hanghae.finalp.config.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsg {
    private String msg;
    private String log;

    public ResultMsg(String msg){
        this.msg = msg;
    }
}