package com.SmartBiz.service;

import com.SmartBiz.dto.AIRequestDto;
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
}
