package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Businesses Entity - Represents a registered business in the SmartBiz
 * platform.
 *
 * Each business has basic contact information and is linked to a subscription
 * plan.
 * A business can have multiple admins, inventory items, sales records, and AI
 * requests.
 *
 * This is the CENTRAL entity that connects most other entities in the system:
 * Admin → belongs to a Business
 * Inventory → belongs to a Business
 * Sales → belongs to a Business
 * AiRequest → belongs to a Business
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Businesses {

    // Primary key - auto-incremented unique identifier for each business
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long business_id;

    // Business name (e.g., "ABC Electronics")
    private String name;

    // Physical address of the business
    private String address;

    // Business contact email
    private String email;

    // Business contact phone number
    private String phone;

    // Business status (e.g., "Active", "Expired", "Trial")
    private String status;

    /**
     * Many-to-One relationship with SubscriptionPlan.
     * Multiple businesses can share the same subscription plan.
     * - FetchType.LAZY : Plan data is loaded only when explicitly accessed
     * - "subscription_id" : Foreign key column in the businesses table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private SubscriptionPlan subscription;

    /**
     * Custom constructor - Creates a Business without setting the ID or
     * subscription.
     * Useful when registering a new business before assigning a plan.
     */
    public Businesses(String name, String address, String email, String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
}
