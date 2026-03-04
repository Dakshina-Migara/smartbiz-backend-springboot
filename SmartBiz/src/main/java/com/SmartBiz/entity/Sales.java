package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleId;

    private String invoiceNumber;

    private Double totalAmount;

    private Integer itemsCount;

    private String paymentMethod;

    private String status = "completed";

    private LocalDateTime saleDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<SaleItem> saleItems;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Payment> payments;

    @PrePersist
    public void generateInvoiceNumber() {
        if (this.invoiceNumber == null) {
            this.invoiceNumber = "INV-" + System.currentTimeMillis() % 10000000 + "-"
                    + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }
    }
}