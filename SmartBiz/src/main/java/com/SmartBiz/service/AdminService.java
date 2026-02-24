package com.SmartBiz.service;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;

import java.util.List;
import java.util.Map;

/**
 * AdminService - Service Interface for Admin-related business logic.
 *
 * This interface defines the CONTRACT (what methods must exist) for admin
 * operations.
 * The actual implementation is in AdminServiceImpl.
 *
 * Why use an interface?
 * 1. Loose coupling : The controller depends on this interface, NOT the
 * implementation.
 * You can swap implementations without changing the controller.
 * 2. Testability : You can create mock implementations for unit testing.
 * 3. Clean architecture: Separates "what to do" (interface) from "how to do it"
 * (impl).
 *
 * Spring automatically injects the AdminServiceImpl when this interface is used
 * because AdminServiceImpl is annotated with @Service and implements this
 * interface.
 */
public interface AdminService {

    /**
     * Retrieves a list of ALL businesses registered in the system.
     * 
     * @return List of BusinessesDto containing business details
     */
    List<BusinessesDto> findAllBusinesses();

    /**
     * Updates an existing subscription plan with new values.
     * 
     * @param id                  the ID of the subscription plan to update
     * @param subscriptionPlanDto the DTO containing the new plan values
     * @return the updated SubscriptionPlanDto, or null if plan not found
     */
    SubscriptionPlanDto updateSubscriptionPlan(Long id, SubscriptionPlanDto subscriptionPlanDto);

    /**
     * Gathers system-wide statistics including:
     * - Total number of businesses
     * - Total number of AI requests
     * - Total AI tokens used across the system
     * 
     * @return a Map with statistic names as keys and their values
     */
    Map<String, Object> getSystemWideStatus();

    /**
     * Retrieves ALL AI request logs across the entire system (global view).
     * 
     * @return List of AIRequestDto containing AI usage logs
     */
    List<AIRequestDto> getGlobalAiLogs();

    /**
     * Creates a new subscription plan in the system.
     * 
     * @param planDto the DTO containing the new plan details
     * @return the created SubscriptionPlanDto with the generated ID
     */
    SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto);
}
