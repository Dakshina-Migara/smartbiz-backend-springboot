package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequestDto {

    private Long requestId;

    private String prompt;

    private String response;

    private String type;

    private Integer tokenUsed;

    private String businessName;

    private String businessOwnerName;

    private LocalDateTime createdAt = LocalDateTime.now();
}
