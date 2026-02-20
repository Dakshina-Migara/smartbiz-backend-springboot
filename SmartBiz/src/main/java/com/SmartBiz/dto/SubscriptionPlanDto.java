package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {
    private Long subscription_id;
    private String plan_name;
    private double price;
    private int ai_token_limit;
    private int max_users;
    private String created_at;
}