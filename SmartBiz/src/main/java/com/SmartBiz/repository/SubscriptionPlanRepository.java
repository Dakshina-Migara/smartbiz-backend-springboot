package com.SmartBiz.repository;

import com.SmartBiz.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SubscriptionPlanRepository - Data Access Layer for the SubscriptionPlan
 * entity.
 *
 * Extends JpaRepository<SubscriptionPlan, Long> which provides built-in CRUD
 * methods:
 * - save() : Insert or update a SubscriptionPlan
 * - findById() : Find a plan by its primary key (subscription_id)
 * - findAll() : Retrieve all subscription plans
 * - deleteById() : Delete a plan by its primary key
 *
 * No custom query methods are defined — uses only inherited JpaRepository
 * methods.
 *
 * Used by AdminServiceImpl to:
 * - Create new subscription plans (save)
 * - Update existing plans (findById + save)
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
}