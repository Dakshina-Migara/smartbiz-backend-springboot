package com.SmartBiz.repository;

import com.SmartBiz.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("SELECT si FROM SaleItem si WHERE si.sale.saleId = :saleId")
    List<SaleItem> findBySaleId(@Param("saleId") Long saleId);

    @Query(value = "SELECT i.product_name, COALESCE(SUM(si.qty), 0) as total_qty " +
            "FROM sale_item si JOIN inventory i ON si.product_id = i.product_id " +
            "JOIN sales s ON si.sale_id = s.sale_id " +
            "WHERE s.business_id = :businessId " +
            "GROUP BY i.product_id, i.product_name " +
            "ORDER BY total_qty DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopSellingProducts(@Param("businessId") Long businessId);
    @Query("SELECT COUNT(si) FROM SaleItem si WHERE si.product.productId = :productId")
    long countByProductId(@Param("productId") Long productId);
}
