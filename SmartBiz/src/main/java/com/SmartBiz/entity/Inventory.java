package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory Entity - Represents a product/item in a business's inventory.
 *
 * Tracks product details like name, current stock level, and price.
 * Each inventory item belongs to a specific business (Many items → One
 * business).
 *
 * This entity is used for:
 * - Adding new products to inventory
 * - Tracking stock levels (for low-stock alerts)
 * - Managing product pricing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Inventory {

    // Primary key - unique identifier for each product
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    // Name of the product (e.g., "Laptop", "USB Cable")
    private String productName;

    // Current quantity in stock (used for low-stock AI insights when < 5)
    private Integer stockLevel;

    // Price of the product in currency units
    private Double price;

    /**
     * Many-to-One relationship with Businesses.
     * Multiple inventory items belong to one business.
     * - FetchType.LAZY : Business data is loaded only when explicitly accessed
     * - "business_id" : Foreign key column in the inventory table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    /**
     * Custom constructor - Creates an Inventory item without setting the ID or
     * business.
     * Useful when adding a new product before associating it with a business.
     */
    public Inventory(String productName, Integer stockLevel, Double price) {
        this.productName = productName;
        this.stockLevel = stockLevel;
        this.price = price;
    }
}