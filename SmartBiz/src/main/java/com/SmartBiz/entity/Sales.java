package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Sales Entity - Represents a single sales transaction for a business.
 *
 * Records the total amount, number of items sold, and the date of the sale.
 * Each sale belongs to one specific business (Many sales → One business).
 *
 * Sales data is used for:
 * - Viewing sales history (ordered by most recent first)
 * - Business analytics and reporting
 * - AI-powered business insights
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sales {

    // Primary key - unique identifier for each sale transaction
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleId;

    // Total monetary amount of the sale (e.g., 150.75)
    private Double totalAmount;

    // Number of items included in this sale transaction
    private Integer itemsCount;

    // Date and time when the sale occurred, defaults to current time
    private LocalDateTime saleDate = LocalDateTime.now();

    /**
     * Many-to-One relationship with Businesses.
     * Multiple sales records belong to one business.
     * - FetchType.LAZY : Business data is loaded only when explicitly accessed
     * - "business_id" : Foreign key column in the sales table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    /**
     * Custom constructor - Creates a Sales record without setting the ID or
     * business.
     * Useful when recording a new sale before linking it to a business.
     */
    public Sales(Double totalAmount, Integer itemsCount, LocalDateTime saleDate) {
        this.totalAmount = totalAmount;
        this.itemsCount = itemsCount;
        this.saleDate = saleDate;
    }
}