package com.SmartBiz.repository;

import com.SmartBiz.entity.Businesses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Businesses, Long> {

        @Query("SELECT b FROM Businesses b LEFT JOIN FETCH b.subscription")
        List<Businesses> findAllWithSubscription();

        Long countByStatus(String status);

        Long countBySubscriptionIsNotNull();

        @Query("SELECT b.subscription.planName, COUNT(b), SUM(b.subscription.price) " +
                        "FROM Businesses b WHERE b.subscription IS NOT NULL GROUP BY b.subscription.planName")
        List<Object[]> countAndRevenueByPlan();

        @Query("SELECT b FROM Businesses b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(b.businessOwnerName) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(b.email) LIKE LOWER(CONCAT('%', :query, '%'))")
        List<Businesses> searchBusinesses(@Param("query") String query);

        @Query("SELECT b FROM Businesses b WHERE b.subscription.subscriptionId = :planId")
        List<Businesses> findBySubscriptionId(@Param("planId") Long planId);
}