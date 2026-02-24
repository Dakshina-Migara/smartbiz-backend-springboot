package com.SmartBiz.repository;

import com.SmartBiz.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("SELECT s FROM Sales s WHERE s.business.business_id = :businessId ORDER BY s.saleDate DESC")
    List<Sales> findByBusinessIdOrderBySaleDateDesc(@Param("businessId") Long businessId);
}