package com.SmartBiz.repository;

import com.SmartBiz.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

        @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId")
        List<Inventory> findByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT COALESCE(SUM(i.price * i.stockLevel), 0) FROM Inventory i WHERE i.business.business_id = :businessId")
        Double calculateInventoryValue(@Param("businessId") Long businessId);

        @Query("SELECT COUNT(i) FROM Inventory i WHERE i.business.business_id = :businessId " +
                        "AND i.stockLevel <= i.minStockLevel AND i.minStockLevel IS NOT NULL AND i.stockLevel > 0")
        Long countLowStock(@Param("businessId") Long businessId);

        @Query("SELECT COUNT(i) FROM Inventory i WHERE i.business.business_id = :businessId AND i.stockLevel = 0")
        Long countOutOfStock(@Param("businessId") Long businessId);

        @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId " +
                        "AND (LOWER(i.productName) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(i.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<Inventory> searchByBusinessId(@Param("businessId") Long businessId, @Param("query") String query);

        @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId AND i.stockLevel = 0")
        List<Inventory> findOutOfStockByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId " +
                        "AND i.stockLevel <= i.minStockLevel AND i.minStockLevel IS NOT NULL AND i.stockLevel > 0")
        List<Inventory> findLowStockByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId " +
                        "AND (i.minStockLevel IS NULL OR i.stockLevel > i.minStockLevel)")
        List<Inventory> findInStockByBusinessId(@Param("businessId") Long businessId);
}