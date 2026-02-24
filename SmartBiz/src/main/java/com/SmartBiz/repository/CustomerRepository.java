package com.SmartBiz.repository;

import com.SmartBiz.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.business.business_id = :businessId")
    List<Customer> findByBusinessId(@Param("businessId") Long businessId);
}
