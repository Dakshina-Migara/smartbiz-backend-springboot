package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private String sku;

    private String category;

    private Double price;

    private Double cost;

    private Integer stockLevel;

    private Integer minStockLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    public Inventory(String productName, Integer stockLevel, Double price) {
        this.productName = productName;
        this.stockLevel = stockLevel;
        this.price = price;
    }
}