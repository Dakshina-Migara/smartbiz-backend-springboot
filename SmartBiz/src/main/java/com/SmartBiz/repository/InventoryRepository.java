package com.SmartBiz.repository;

import com.SmartBiz.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * InventoryRepository - Data Access Layer for the Inventory entity.
 *
 * Extends JpaRepository<Inventory, Long> which provides built-in CRUD methods.
 *
 * Note: This repository does NOT have @Repository annotation, but it still
 * works
 * because Spring Data JPA automatically detects interfaces extending
 * JpaRepository
 * and registers them as beans. @Repository is optional but recommended for
 * clarity.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Custom JPQL query to find all inventory items belonging to a specific
     * business.
     *
     * JPQL: "SELECT i FROM Inventory i WHERE i.business.business_id = :businessId"
     * → Navigates the Inventory → Businesses relationship using the object graph
     * → Returns all Inventory items where the parent business's ID matches
     *
     * Used by BusinessOwnerServiceImpl to:
     * - List all products for a business (getAllInventory)
     * - Generate AI insights about low-stock items (generateAiInsight)
     *
     * @param businessId the ID of the business to get inventory for
     * @return List of Inventory items belonging to the specified business
     */
    @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId")
    List<Inventory> findByBusinessId(@Param("businessId") Long businessId);
}