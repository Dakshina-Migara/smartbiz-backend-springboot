package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {

    private Long subscriptionId;

    @NotBlank(message = "Plan name is required")
    private String planName;

    @Min(value = 0, message = "Price must be zero or positive")
    private double price;

    @Min(value = 1, message = "AI token limit must be at least 1")
    private int aiTokenLimit;

    @Min(value = 1, message = "Max users must be at least 1")
    private int maxUsers;

    private String billingCycle;

    private String features;

    private String createdAt;

    private Long activeSubscribers;

    private Double monthlyRevenue;
}