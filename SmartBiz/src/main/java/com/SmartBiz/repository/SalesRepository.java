package com.SmartBiz.repository;

import com.SmartBiz.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("SELECT s FROM Sales s WHERE s.business.business_id = :businessId ORDER BY s.saleDate DESC")
    List<Sales> findByBusinessIdOrderBySaleDateDesc(@Param("businessId") Long businessId);

    @Query("SELECT s FROM Sales s WHERE s.business.business_id = :businessId " +
            "AND (LOWER(s.invoiceNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.customer.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Sales> searchByBusinessId(@Param("businessId") Long businessId, @Param("query") String query);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sales s WHERE s.business.business_id = :businessId")
    Double sumTotalAmountByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT s FROM Sales s WHERE s.business.business_id = :businessId AND s.status = :status ORDER BY s.saleDate DESC")
    List<Sales> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") String status);
}