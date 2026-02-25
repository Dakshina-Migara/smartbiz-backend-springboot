package com.SmartBiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long transactionId;

    @NotBlank(message = "Type is required (income or expense)")
    private String type;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Amount is required")
    private Double amount;

    private LocalDate date;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}
