package com.SmartBiz.service;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.ActivityLogDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Map;

public interface AdminService {

    List<BusinessesDto> findAllBusinesses();

    SubscriptionPlanDto updateSubscriptionPlan(@NonNull Long id, @NonNull SubscriptionPlanDto subscriptionPlanDto);

    Map<String, Object> getSystemWideStatus();

    List<AIRequestDto> getGlobalAiLogs();

    SubscriptionPlanDto createSubscriptionPlan(@NonNull SubscriptionPlanDto planDto);

    Map<String, Object> getDashboardStats();

    List<BusinessesDto> searchBusinesses(@NonNull String query);

    void deleteBusiness(@NonNull Long businessId);

    void deleteAccount(@NonNull Long adminId);

    BusinessesDto updateAccount(@NonNull Long adminId, @NonNull BusinessesDto dto);

    List<ActivityLogDto> getActivityLogs();

    List<SubscriptionPlanDto> getAllSubscriptionPlans();

    void deleteSubscriptionPlan(@NonNull Long id);
}
