package com.SmartBiz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CustomerDto - Data Transfer Object for Customer data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long customerId;

    @NotBlank(message = "Customer name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private Long businessId;
}
