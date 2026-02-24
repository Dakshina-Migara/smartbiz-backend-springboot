package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AiRequest Entity - Stores AI-related requests made by users in the system.
 *
 * This entity tracks every AI prompt/response interaction, including:
 * - What the user asked (prompt)
 * - What the AI returned (response)
 * - How many tokens were consumed (tokenUsed)
 * - When the request was made (createdAt)
 *
 * Relationships:
 * - Each AI request belongs to ONE business (ManyToOne)
 * - Each AI request was made by ONE admin/user (ManyToOne)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AiRequest {

    // Primary key - auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long request_Id;

    // The AI prompt/question submitted by the user
    private String prompt;

    // The AI-generated response to the prompt
    private String response;

    // Number of AI tokens consumed for this request (used for billing/limits)
    private Integer tokenUsed;

    // Timestamp when the AI request was created, defaults to current time
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Many-to-One relationship with Businesses.
     * Multiple AI requests can be made by the same business.
     * - FetchType.LAZY: Business data is loaded only when accessed (performance
     * optimization)
     * - "business_id" is the foreign key column in the ai_request table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    /**
     * Many-to-One relationship with Admin (the user who made the request).
     * Multiple AI requests can be made by the same admin user.
     * - "user_id" is the foreign key column in the ai_request table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Admin user;

    /**
     * Custom constructor - Creates an AiRequest without setting relationships.
     * Useful when creating a new request before linking it to a business/user.
     */
    public AiRequest(String prompt, String response, Integer tokenUsed, LocalDateTime createdAt) {
        this.prompt = prompt;
        this.response = response;
        this.tokenUsed = tokenUsed;
        this.createdAt = createdAt;
    }
}