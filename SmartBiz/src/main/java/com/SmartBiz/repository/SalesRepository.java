package com.SmartBiz.repository;

import com.SmartBiz.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * SalesRepository - Data Access Layer for the Sales entity.
 *
 * Extends JpaRepository<Sales, Long> which provides built-in CRUD methods.
 *
 * Note: Like InventoryRepository, this does not have @Repository but still
 * works because Spring auto-detects JpaRepository sub-interfaces.
 */
public interface SalesRepository extends JpaRepository<Sales, Long> {

    /**
     * Custom JPQL query to find all sales for a specific business,
     * ordered by sale date in DESCENDING order (most recent sales first).
     *
     * JPQL: "SELECT s FROM Sales s WHERE s.business.business_id = :businessId
     * ORDER BY s.saleDate DESC"
     * → Filters sales by the parent business's ID
     * → Sorts results so the newest sales appear first
     *
     * Used by BusinessOwnerServiceImpl.getSalesHistory() to display
     * a business's sales history with the latest transactions on top.
     *
     * @param businessId the ID of the business to get sales history for
     * @return List of Sales records sorted by date (newest first)
     */
    @Query("SELECT s FROM Sales s WHERE s.business.business_id = :businessId ORDER BY s.saleDate DESC")
    List<Sales> findByBusinessIdOrderBySaleDateDesc(@Param("businessId") Long businessId);

}