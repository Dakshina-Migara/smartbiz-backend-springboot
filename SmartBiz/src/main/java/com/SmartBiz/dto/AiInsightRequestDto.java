package com.SmartBiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiInsightRequestDto {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    @NotBlank(message = "Type is required (business_report, email, marketing)")
    private String type;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}
