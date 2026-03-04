package com.SmartBiz.repository;

import com.SmartBiz.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

        @Query("SELECT s FROM Sales s WHERE s.business.businessId = :businessId ORDER BY s.saleDate DESC")
        List<Sales> findByBusinessIdOrderBySaleDateDesc(@Param("businessId") Long businessId);

        @Query("SELECT s FROM Sales s WHERE s.business.businessId = :businessId " +
                        "AND (LOWER(s.invoiceNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(s.customer.name) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<Sales> searchByBusinessId(@Param("businessId") Long businessId, @Param("query") String query);

        @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sales s WHERE s.business.businessId = :businessId")
        Double sumTotalAmountByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT s FROM Sales s WHERE s.business.businessId = :businessId AND s.status = :status ORDER BY s.saleDate DESC")
        List<Sales> findByBusinessIdAndStatus(@Param("businessId") Long businessId, @Param("status") String status);

        @Query(value = "SELECT DATE(sale_date) as sale_day, COALESCE(SUM(total_amount), 0) as daily_total " +
                        "FROM sales WHERE business_id = :businessId " +
                        "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                        "GROUP BY DATE(sale_date) ORDER BY sale_day", nativeQuery = true)
        List<Object[]> dailySalesLast30Days(@Param("businessId") Long businessId);

        @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM sales " +
                        "WHERE business_id = :businessId " +
                        "AND MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE())", nativeQuery = true)
        Double sumCurrentMonthRevenue(@Param("businessId") Long businessId);

        @Query("SELECT COUNT(s) FROM Sales s WHERE s.business.businessId = :businessId")
        Long countByBusinessId(@Param("businessId") Long businessId);
}
