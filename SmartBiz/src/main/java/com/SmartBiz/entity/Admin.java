package com.SmartBiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Admin Entity - Represents an admin/user in the SmartBiz system.
 *
 * This entity maps to the "admin" table in the MySQL database.
 * Each admin belongs to a specific business (Many Admins → One Business).
 *
 * Lombok Annotations:
 * - @Data : Auto-generates getters, setters, toString(), equals(), hashCode()
 * - @NoArgsConstructor : Generates a no-argument constructor (required by
 * JPA/Hibernate)
 * - @AllArgsConstructor : Generates a constructor with all fields as parameters
 *
 * JPA Annotations:
 * - @Entity : Marks this class as a JPA entity (maps to a database table)
 * - @Id : Marks the primary key field
 * - @GeneratedValue : Auto-increments the primary key value in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin {

    // Primary key - auto-incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_Id;

    // Admin's full name
    private String name;

    // Admin's email address (used for login/identification)
    private String email;

    // Admin's password (should be encrypted before storing)
    private String password;

    // Admin's role (e.g., "SUPER_ADMIN", "BUSINESS_OWNER", etc.)
    private String role;

    // Timestamp when the admin account was created, defaults to current time
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Many-to-One relationship: Multiple admins can belong to one business.
     * - FetchType.LAZY: The business data is NOT loaded until you explicitly access
     * it
     * (improves performance by avoiding unnecessary database queries)
     * - @JoinColumn : Specifies "business_id" as the foreign key column in the
     * admin table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Businesses business;

    /**
     * Custom constructor - Creates an Admin without setting the ID or business.
     * Useful when creating a new admin before associating it with a business.
     */
    public Admin(String name, String email, String password, String role, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }
}