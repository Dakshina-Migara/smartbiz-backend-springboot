package com.SmartBiz.repository;

import com.SmartBiz.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.business.businessId = :businessId")
    List<Customer> findByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.business.businessId = :businessId")
    Long countByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT c FROM Customer c WHERE c.business.businessId = :businessId " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR c.phone LIKE CONCAT('%', :query, '%'))")
    List<Customer> searchByBusinessId(@Param("businessId") Long businessId, @Param("query") String query);

    @Query("SELECT c FROM Customer c WHERE c.business.businessId = :businessId AND LOWER(c.name) = LOWER(:name)")
    java.util.Optional<Customer> findByBusinessIdAndNameIgnoreCase(@Param("businessId") Long businessId,
            @Param("name") String name);
}
