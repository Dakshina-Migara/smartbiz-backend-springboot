package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * SalesDto - Data Transfer Object for Sales transaction data.
 * Includes validation annotations to ensure valid sale data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesDto {

    private Long saleId;

    // Total amount must be positive
    @NotNull(message = "Total amount is required")
    @Min(value = 0, message = "Total amount cannot be negative")
    private Double totalAmount;

    // Items count must be at least 1
    @NotNull(message = "Items count is required")
    @Min(value = 1, message = "Items count must be at least 1")
    private Integer itemsCount;

    private LocalDateTime saleDate;

    // Business ID is required to link sale to a business
    @NotNull(message = "Business ID is required")
    private Long business_id;
}