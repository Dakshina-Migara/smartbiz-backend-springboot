package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessesDto {
    private Long business_id;
    private String name;
    private String address;
    private String email;
    private String phone;
}