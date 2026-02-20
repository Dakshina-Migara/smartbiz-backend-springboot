package com.SmartBiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscription_id;
    private String plan_name;
    private double price;
    private int ai_token_limit;
    private int max_users;
    private String created_at;

    public SubscriptionPlan(String plan_name, double price, int ai_token_limit, int max_users, String created_at) {
        this.plan_name = plan_name;
        this.price = price;
        this.ai_token_limit = ai_token_limit;
        this.max_users = max_users;
        this.created_at = created_at;
    }
}
