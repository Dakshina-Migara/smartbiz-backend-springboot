package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SubscriptionPlanDto - Data Transfer Object for Subscription Plan data.
 * Now includes validation annotations to prevent invalid data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {

    private Long subscription_id;

    // Plan name is required and cannot be blank
    @NotBlank(message = "Plan name is required")
    private String plan_name;

    // Price must be zero or positive
    @Min(value = 0, message = "Price must be zero or positive")
    private double price;

    // AI token limit must be at least 1
    @Min(value = 1, message = "AI token limit must be at least 1")
    private int ai_token_limit;

    // Max users must be at least 1
    @Min(value = 1, message = "Max users must be at least 1")
    private int max_users;

    private String created_at;
}