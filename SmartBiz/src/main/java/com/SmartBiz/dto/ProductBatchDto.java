package com.SmartBiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ProductBatchDto - Data Transfer Object for product batch data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchDto {

    private Long batchId;

    @NotBlank(message = "Batch number is required")
    private String batchNo;

    private LocalDate expDate;

    @NotNull(message = "Cost price is required")
    private Double costPrice;

    private LocalDateTime createdAt;

    @NotNull(message = "Product ID is required")
    private Long productId;
}
