package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessesDto {

    private Long businessId;

    private Long adminId;

    private String role;

    private String businessOwnerName;

    private String name;

    private String address;

    private String email;

    private String phone;

    private String status;

    private String planName;

    private Double revenue;

    private Long aiUsage;
    private Integer aiTokenLimit;
    private Long aiUsageTotal;

    private LocalDateTime registeredDate;
}