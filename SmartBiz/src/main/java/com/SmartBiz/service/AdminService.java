package com.SmartBiz.service;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.ActivityLogDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;

import java.util.List;
import java.util.Map;

public interface AdminService {

    List<BusinessesDto> findAllBusinesses();

    SubscriptionPlanDto updateSubscriptionPlan(Long id, SubscriptionPlanDto subscriptionPlanDto);

    Map<String, Object> getSystemWideStatus();

    List<AIRequestDto> getGlobalAiLogs();

    SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto);

    Map<String, Object> getDashboardStats();

    List<BusinessesDto> searchBusinesses(String query);

    void deleteBusiness(Long businessId);

    List<ActivityLogDto> getActivityLogs();

    List<SubscriptionPlanDto> getAllSubscriptionPlans();

    void deleteSubscriptionPlan(Long id);
}
