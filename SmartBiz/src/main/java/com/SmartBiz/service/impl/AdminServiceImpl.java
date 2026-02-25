package com.SmartBiz.service.impl;

import com.SmartBiz.dto.*;
import com.SmartBiz.entity.*;
import com.SmartBiz.exception.ResourceNotFoundException;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final BusinessRepository businessRepository;
    private final AiRequestRepository aiRequestRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImpl(BusinessRepository businessRepository, AiRequestRepository aiRequestRepository,
            SubscriptionPlanRepository subscriptionPlanRepository, ActivityLogRepository activityLogRepository,
            AdminRepository adminRepository) {
        this.businessRepository = businessRepository;
        this.aiRequestRepository = aiRequestRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.activityLogRepository = activityLogRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessesDto> findAllBusinesses() {
        try {
            return businessRepository.findAll().stream().map(this::mapToBusinessDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching businesses: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessesDto> searchBusinesses(String query) {
        try {
            return businessRepository.searchBusinesses(query).stream()
                    .map(this::mapToBusinessDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching businesses with query '{}': {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public BusinessesDto suspendBusiness(Long businessId) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
            business.setStatus("suspended");
            Businesses updated = businessRepository.save(business);
            log.info("Suspended business id: {}", businessId);
            return mapToBusinessDto(updated);
        } catch (Exception e) {
            log.error("Error suspending business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to suspend business: " + e.getMessage());
        }
    }

    @Override
    public BusinessesDto activateBusiness(Long businessId) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
            business.setStatus("active");
            Businesses updated = businessRepository.save(business);
            log.info("Activated business id: {}", businessId);
            return mapToBusinessDto(updated);
        } catch (Exception e) {
            log.error("Error activating business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to activate business: " + e.getMessage());
        }
    }

    @Override
    public void deleteBusiness(Long businessId) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

            // cascades in Businesses entity handle the rest
            businessRepository.delete(business);
            log.info("Successfully deleted business id: {} and all its associated data via JPA cascades", businessId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException(
                    "Failed to delete business. This is likely because the business has existing transaction history or records. Consider suspending the business instead.");
        }
    }

    @Override
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto) {
        try {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setPlanName(planDto.getPlanName());
            plan.setPrice(planDto.getPrice());
            plan.setAiTokenLimit(planDto.getAiTokenLimit());
            plan.setMaxUsers(planDto.getMaxUsers());
            plan.setBillingCycle(planDto.getBillingCycle());
            plan.setFeatures(planDto.getFeatures());

            SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
            log.info("Created new subscription plan: {}", saved.getPlanName());
            return mapToSubscriptionDto(saved);
        } catch (Exception e) {
            log.error("Error creating subscription plan: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create subscription plan: " + e.getMessage());
        }
    }

    @Override
    public SubscriptionPlanDto updateSubscriptionPlan(Long id, SubscriptionPlanDto planDto) {
        try {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));

            plan.setPlanName(planDto.getPlanName());
            plan.setPrice(planDto.getPrice());
            plan.setAiTokenLimit(planDto.getAiTokenLimit());
            plan.setMaxUsers(planDto.getMaxUsers());
            plan.setBillingCycle(planDto.getBillingCycle());
            plan.setFeatures(planDto.getFeatures());

            SubscriptionPlan updated = subscriptionPlanRepository.save(plan);
            log.info("Updated subscription plan id: {}", id);
            return mapToSubscriptionDto(updated);
        } catch (Exception e) {
            log.error("Error updating subscription plan id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update subscription plan: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemWideStatus() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalBusinesses", businessRepository.count());
            stats.put("totalAiRequests", aiRequestRepository.count());
            Long totalTokens = aiRequestRepository.sumAllTokens();
            stats.put("totalTokensUsed", totalTokens != null ? totalTokens : 0);
            return stats;
        } catch (Exception e) {
            log.error("Error fetching system statistics: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AIRequestDto> getGlobalAiLogs() {
        try {
            return aiRequestRepository.findAll().stream().map(this::mapToAiDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching AI logs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        try {
            Map<String, Object> stats = new LinkedHashMap<>();

            stats.put("totalBusinesses", businessRepository.count());
            Long activeCount = businessRepository.countByStatus("active");
            stats.put("activeBusinesses", activeCount != null ? activeCount : 0);

            Long totalSubscribers = businessRepository.countBySubscriptionIsNotNull();
            stats.put("totalSubscribers", totalSubscribers != null ? totalSubscribers : 0);

            Long tokensThisMonth = aiRequestRepository.sumTokensThisMonth();
            stats.put("totalTokensThisMonth", tokensThisMonth != null ? tokensThisMonth : 0);

            List<Object[]> planStats = businessRepository.countAndRevenueByPlan();
            List<Map<String, Object>> revenueByPlan = new ArrayList<>();
            List<Map<String, Object>> subscribersByPlan = new ArrayList<>();
            double monthlyRevenue = 0;

            for (Object[] row : planStats) {
                String planName = (String) row[0];
                Long count = (Long) row[1];
                Double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

                Map<String, Object> revenueEntry = new LinkedHashMap<>();
                revenueEntry.put("planName", planName);
                revenueEntry.put("revenue", revenue);
                revenueByPlan.add(revenueEntry);

                Map<String, Object> subscriberEntry = new LinkedHashMap<>();
                subscriberEntry.put("planName", planName);
                subscriberEntry.put("count", count);
                subscribersByPlan.add(subscriberEntry);

                monthlyRevenue += revenue;
            }

            stats.put("monthlyRevenue", monthlyRevenue);
            stats.put("revenueByPlan", revenueByPlan);
            stats.put("subscribersByPlan", subscribersByPlan);

            List<Object[]> dailyTokens = aiRequestRepository.dailyTokenUsageLast30Days();
            List<Map<String, Object>> dailyAiTokenUsage = new ArrayList<>();
            for (Object[] row : dailyTokens) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", row[0] != null ? row[0].toString() : null);
                entry.put("tokens", row[1] != null ? ((Number) row[1]).longValue() : 0);
                dailyAiTokenUsage.add(entry);
            }
            stats.put("dailyAiTokenUsage", dailyAiTokenUsage);

            return stats;
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    private BusinessesDto mapToBusinessDto(Businesses b) {
        BusinessesDto dto = new BusinessesDto();
        dto.setBusinessId(b.getBusinessId());
        dto.setBusinessOwnerName(b.getBusinessOwnerName());
        dto.setName(b.getName());
        dto.setAddress(b.getAddress());
        dto.setEmail(b.getEmail());
        dto.setPhone(b.getPhone());
        dto.setStatus(b.getStatus());
        dto.setRegisteredDate(b.getRegisteredDate());

        adminRepository.findByBusinessId(b.getBusinessId())
                .ifPresent(owner -> dto.setBusinessOwnerName(owner.getName()));

        if (b.getSubscription() != null) {
            dto.setPlanName(b.getSubscription().getPlanName());
            dto.setRevenue(b.getSubscription().getPrice());
        }

        Long aiUsage = aiRequestRepository.sumTokensByBusinessId(b.getBusinessId());
        dto.setAiUsage(aiUsage != null ? aiUsage : 0L);

        return dto;
    }

    private SubscriptionPlanDto mapToSubscriptionDto(SubscriptionPlan s) {
        SubscriptionPlanDto dto = new SubscriptionPlanDto();
        dto.setSubscriptionId(s.getSubscriptionId());
        dto.setPlanName(s.getPlanName());
        dto.setPrice(s.getPrice());
        dto.setAiTokenLimit(s.getAiTokenLimit());
        dto.setMaxUsers(s.getMaxUsers());
        dto.setBillingCycle(s.getBillingCycle());
        dto.setFeatures(s.getFeatures());
        dto.setCreatedAt(s.getCreatedAt());

        Long subscribers = subscriptionPlanRepository.countSubscribersByPlanId(s.getSubscriptionId());
        dto.setActiveSubscribers(subscribers != null ? subscribers : 0L);

        Double revenue = subscriptionPlanRepository.calculateRevenueByPlanId(s.getSubscriptionId());
        dto.setMonthlyRevenue(revenue != null ? revenue : 0.0);

        return dto;
    }

    private AIRequestDto mapToAiDto(AiRequest a) {
        AIRequestDto dto = new AIRequestDto();
        dto.setRequestId(a.getRequestId());
        dto.setPrompt(a.getPrompt());
        dto.setResponse(a.getResponse());
        dto.setTokenUsed(a.getTokenUsed());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogDto> getActivityLogs() {
        try {
            return activityLogRepository.findAllByOrderByTimestampDesc().stream()
                    .map(this::mapToActivityLogDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching activity logs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDto> getAllSubscriptionPlans() {
        try {
            return subscriptionPlanRepository.findAll().stream()
                    .map(this::mapToSubscriptionDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching subscription plans: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteSubscriptionPlan(Long id) {
        try {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with id: " + id));

            // Unlink businesses using this plan before deletion
            List<Businesses> businesses = businessRepository.findBySubscriptionId(id);
            for (Businesses b : businesses) {
                b.setSubscription(null);
                businessRepository.save(b);
            }

            subscriptionPlanRepository.delete(plan);
            log.info("Deleted subscription plan id: {}", id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting subscription plan id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete subscription plan: " + e.getMessage());
        }
    }

    private ActivityLogDto mapToActivityLogDto(ActivityLog log) {
        ActivityLogDto dto = new ActivityLogDto();
        dto.setLogId(log.getLogId());
        dto.setTimestamp(log.getTimestamp());
        dto.setFeature(log.getFeature());
        dto.setAction(log.getAction());
        dto.setAiTokens(log.getAiTokens());
        if (log.getBusiness() != null) {
            dto.setBusinessName(log.getBusiness().getName());
        }
        return dto;
    }
}