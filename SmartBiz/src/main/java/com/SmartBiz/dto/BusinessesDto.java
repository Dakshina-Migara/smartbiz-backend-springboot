package com.SmartBiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BusinessesDto - Data Transfer Object for Business data.
 *
 * Transfers business information between the Controller and Service layers.
 * Excludes the SubscriptionPlan relationship object to keep the API response
 * clean.
 *
 * Used by AdminController.getAllBusinesses() to return a list of all businesses
 * without exposing internal JPA entity relationships.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessesDto {

    // Unique identifier of the business
    private Long business_id;

    // Business name
    private String name;

    // Physical address of the business
    private String address;

    // Business contact email
    private String email;

    // Business contact phone number
    private String phone;
}