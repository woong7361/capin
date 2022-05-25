package com.hanghae.finalp.entity.dto.other;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsg {
    private String result;
    private String log;

    public ResultMsg(String result){
        this.result = result;
    }
}