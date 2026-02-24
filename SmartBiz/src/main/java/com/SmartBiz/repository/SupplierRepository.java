package com.SmartBiz.repository;

import com.SmartBiz.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s WHERE s.business.business_id = :businessId")
    List<Supplier> findByBusinessId(@Param("businessId") Long businessId);
}
