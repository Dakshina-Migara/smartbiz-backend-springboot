package com.SmartBiz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

    private Long supplierId;

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String company;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private Long businessId;
}
