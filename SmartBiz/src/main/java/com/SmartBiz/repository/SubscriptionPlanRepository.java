package com.SmartBiz.repository;

import com.SmartBiz.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    @Query("SELECT COUNT(b) FROM Businesses b WHERE b.subscription.subscriptionId = :planId")
    Long countSubscribersByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(b) * s.price FROM Businesses b JOIN b.subscription s WHERE s.subscriptionId = :planId")
    Double calculateRevenueByPlanId(@Param("planId") Long planId);
}