package com.SmartBiz.repository;

import com.SmartBiz.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.business.business_id = :businessId ORDER BY p.paymentDate DESC")
    List<Payment> findByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT p FROM Payment p WHERE p.sale.saleId = :saleId")
    List<Payment> findBySaleId(@Param("saleId") Long saleId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.business.business_id = :businessId")
    Double sumAmountByBusinessId(@Param("businessId") Long businessId);
}
