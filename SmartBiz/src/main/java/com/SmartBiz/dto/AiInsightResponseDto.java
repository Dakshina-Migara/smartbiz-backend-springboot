package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiInsightResponseDto {

    private Long requestId;
    private String prompt;
    private String type;
    private String response;
    private Integer tokenUsed;
    private LocalDateTime createdAt;
}
