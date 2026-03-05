package com.SmartBiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesDto {

    private Long saleId;

    private String invoiceNumber;

    private Long customerId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    @NotNull(message = "Total amount is required")
    @Min(value = 0, message = "Total amount cannot be negative")
    private Double totalAmount;

    @NotNull(message = "Items count is required")
    @Min(value = 1, message = "Items count must be at least 1")
    private Integer itemsCount;

    private String paymentMethod;

    private String status;

    private LocalDateTime saleDate;

    @NotNull(message = "Business ID is required")
    private Long businessId;

    private List<SaleItemDto> items;
}