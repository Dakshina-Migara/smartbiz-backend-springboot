package com.SmartBiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
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

    private String billing_cycle;

    @Column(columnDefinition = "TEXT")
    private String features;

    private String created_at;
}
