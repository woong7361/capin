package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchWordDto {
    private String title;
    private List<Address> addressList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String address;
    }


}



