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

    private Long subscription_id;

    @NotBlank(message = "Plan name is required")
    private String plan_name;

    @Min(value = 0, message = "Price must be zero or positive")
    private double price;

    @Min(value = 1, message = "AI token limit must be at least 1")
    private int ai_token_limit;

    @Min(value = 1, message = "Max users must be at least 1")
    private int max_users;

    private String billing_cycle;

    private String features;

    private String created_at;

    private Long activeSubscribers;

    private Double monthlyRevenue;
}