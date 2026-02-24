package com.SmartBiz.repository;

import com.SmartBiz.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.business.business_id = :businessId")
    List<Inventory> findByBusinessId(@Param("businessId") Long businessId);
}