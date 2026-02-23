package com.SmartBiz.service.impl;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import com.SmartBiz.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Override
    public List<BusinessesDto> findAllBusinesses() {
        return List.of();
    }

    @Override
    public SubscriptionPlanDto updateSubscriptionPlan(Long id, SubscriptionPlanDto subscriptionPlanDto) {
        return null;
    }

    @Override
    public Map<String, Object> getSystemWideStatus() {
        return Map.of();
    }

    @Override
    public List<AIRequestDto> getGlobalAiLogs() {
        return List.of();
    }

    @Override
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto) {
        return null;
    }
}
