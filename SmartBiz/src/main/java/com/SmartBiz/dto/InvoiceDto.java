package com.SmartBiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * InvoiceDto - Data Transfer Object for Invoice data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Long invoiceId;
    private String invoiceNumber;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerEmail;
    private LocalDateTime issuedDate;
    private LocalDateTime createdAt;

    @NotNull(message = "Sale ID is required")
    private Long saleId;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}
