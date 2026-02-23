package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesDto {
    private Long saleId;
    private Double totalAmount;
    private Integer itemsCount;
    private LocalDateTime saleDate;
    private Long business_id;
}