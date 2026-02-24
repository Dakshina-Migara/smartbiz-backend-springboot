package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long request_Id;

    private String prompt;

    private String response;

    private Integer tokenUsed;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Admin user;

    public AiRequest(String prompt, String response, Integer tokenUsed, LocalDateTime createdAt) {
        this.prompt = prompt;
        this.response = response;
        this.tokenUsed = tokenUsed;
        this.createdAt = createdAt;
    }
}