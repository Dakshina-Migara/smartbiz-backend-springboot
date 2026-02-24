package com.SmartBiz.repository;

import com.SmartBiz.entity.Businesses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Businesses, Long> {

    Long countByStatus(String status);

    Long countBySubscriptionIsNotNull();

    @Query("SELECT b.subscription.plan_name, COUNT(b), SUM(b.subscription.price) " +
            "FROM Businesses b WHERE b.subscription IS NOT NULL GROUP BY b.subscription.plan_name")
    List<Object[]> countAndRevenueByPlan();
}