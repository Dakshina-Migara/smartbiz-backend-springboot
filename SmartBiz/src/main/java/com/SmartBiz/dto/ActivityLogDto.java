package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {

    private Long logId;

    private LocalDateTime timestamp;

    private String businessName;

    private String feature;

    private String action;

    private Integer aiTokens;
}
