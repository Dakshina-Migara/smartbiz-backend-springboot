package com.SmartBiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileSaleRequestDto {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerEmail;
    private String customerPhone;

    @NotEmpty(message = "At least one item is required")
    private List<SaleItemDto> items;

    private String paymentMethod = "cash";
    private String status = "completed";
}
