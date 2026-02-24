package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AIRequestDto - Data Transfer Object for AI Request data.
 *
 * DTOs are used to transfer data between the Controller layer and the
 * Service layer. They act as a "filter" to:
 * 1. Hide sensitive entity fields from the API response
 * 2. Decouple the API response format from the database structure
 * 3. Prevent exposing internal entity relationships (like business, user
 * objects)
 *
 * This DTO carries AI request information WITHOUT the Businesses or Admin
 * relationship objects — only the essential fields are exposed to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequestDto {

    // Unique identifier of the AI request
    private Long request_Id;

    // The AI prompt/question submitted by the user
    private String prompt;

    // The AI-generated response
    private String response;

    // Number of AI tokens consumed for this request
    private Integer tokenUsed;

    // Timestamp when the request was made
    private LocalDateTime createdAt = LocalDateTime.now();
}
