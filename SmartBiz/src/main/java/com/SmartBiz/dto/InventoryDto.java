package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InventoryDto - Data Transfer Object for Inventory/Product data.
 * Includes validation annotations to ensure valid product data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long productId;

    // Product name is required
    @NotBlank(message = "Product name is required")
    private String productName;

    // Stock level must be zero or positive
    @NotNull(message = "Stock level is required")
    @Min(value = 0, message = "Stock level cannot be negative")
    private Integer stockLevel;

    // Price must be positive
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    // Business ID is required to link product to a business
    @NotNull(message = "Business ID is required")
    private Long business_id;
}