package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ProductBatch Entity - Tracks product batches with expiry dates and cost
 * prices.
 * Useful for businesses that deal with perishable goods or batch tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    private String batchNo;
    private LocalDate expDate;
    private Double costPrice;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Inventory product;
}
