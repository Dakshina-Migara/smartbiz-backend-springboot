package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String sku;

    private String category;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    private Double cost;

    @NotNull(message = "Stock level is required")
    @Min(value = 0, message = "Stock level cannot be negative")
    private Integer stockLevel;

    private Integer minStockLevel;

    private String stockStatus;

    private Double stockValue;

    private Long supplierId;

    private String supplierName;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}