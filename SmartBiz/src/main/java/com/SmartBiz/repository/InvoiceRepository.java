package com.SmartBiz.repository;

import com.SmartBiz.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE i.business.business_id = :businessId ORDER BY i.createdAt DESC")
    List<Invoice> findByBusinessId(@Param("businessId") Long businessId);
}
