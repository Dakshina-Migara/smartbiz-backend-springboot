package com.SmartBiz.service.impl;

import com.SmartBiz.dto.*;
import com.SmartBiz.entity.*;
import com.SmartBiz.exception.ResourceNotFoundException;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final BusinessRepository businessRepository;
    private final AiRequestRepository aiRequestRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BusinessesDto> findAllBusinesses() {
        try {
            // 2. Fetch all admins (includes owners and system admins)
            List<Admin> allAdmins = adminRepository.findAll();

            // 3. Bulk fetch AI usage
            Map<Long, Long> aiUsageMap = aiRequestRepository.sumTokensGroupedByBusiness().stream()
                    .collect(Collectors.toMap(
                            row -> (Long) row[0],
                            row -> row[1] != null ? ((Number) row[1]).longValue() : 0L));

            return allAdmins.stream()
                    .map(admin -> mapFullAccount(admin, aiUsageMap))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all accounts: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessesDto> searchBusinesses(String query) {
        try {
            List<Businesses> businesses = businessRepository.searchBusinesses(query);

            Map<Long, Long> aiUsageMap = aiRequestRepository.sumTokensGroupedByBusiness().stream()
                    .collect(Collectors.toMap(
                            row -> (Long) row[0],
                            row -> row[1] != null ? ((Number) row[1]).longValue() : 0L));

            return businesses.stream()
                    .filter(b -> b.getAdmins() != null && !b.getAdmins().isEmpty())
                    .map(b -> mapFullAccount(b.getAdmins().get(0), aiUsageMap)) // Assuming 1-1 link for search results
                                                                                // if possible or find admin
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching businesses with query '{}': {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public void deleteBusiness(Long businessId) {
        try {
            // Find businesses with that admin id? (wait user said businessId but linked to
            // Admin)
            // Actually, currently deleteBusiness works by businessId in the controller.
            // Let's assume it should also delete the associated Admin if it's the owner?
            // Existing logic works on businessId.
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

            businessRepository.delete(business);
            log.info("Deleted business id: {}", businessId);
        } catch (Exception e) {
            log.error("Error deleting business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete business: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public BusinessesDto updateAccount(Long adminId, BusinessesDto dto) {
        try {
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));

            // Update user/owner basic details
            admin.setName(dto.getBusinessOwnerName());
            admin.setEmail(dto.getEmail());
            adminRepository.save(admin);

            // Update linked business details if it exists
            Businesses business = admin.getBusiness();
            if (business != null) {
                business.setName(dto.getName());
                business.setAddress(dto.getAddress());
                business.setPhone(dto.getPhone());
                business.setEmail(dto.getEmail()); // stay in sync
                business.setBusinessOwnerName(dto.getBusinessOwnerName());
                businessRepository.save(business);
            }

            log.info("Successfully updated account for adminId: {}", adminId);
            return mapFullAccount(admin, null);
        } catch (Exception e) {
            log.error("Error updating account id {}: {}", adminId, e.getMessage(), e);
            throw new RuntimeException("Failed to update account: " + e.getMessage());
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

    private BusinessesDto mapFullAccount(Admin admin, Map<Long, Long> aiUsageMap) {
        BusinessesDto dto = new BusinessesDto();
        dto.setAdminId(admin.getAdminId());
        dto.setRole(admin.getRole());
        dto.setBusinessOwnerName(admin.getName());
        dto.setEmail(admin.getEmail());
        dto.setRegisteredDate(admin.getCreatedAt());

        Businesses b = admin.getBusiness();
        if (b != null) {
            dto.setBusinessId(b.getBusinessId());
            dto.setName(b.getName());
            dto.setAddress(b.getAddress());
            dto.setPhone(b.getPhone());
            dto.setStatus(b.getStatus() != null ? b.getStatus() : "active");

            if (b.getSubscription() != null) {
                dto.setPlanName(b.getSubscription().getPlanName());
                dto.setRevenue(b.getSubscription().getPrice());
            }

            if (aiUsageMap != null) {
                dto.setAiUsage(aiUsageMap.getOrDefault(b.getBusinessId(), 0L));
            } else {
                Long usage = aiRequestRepository.sumTokensByBusinessId(b.getBusinessId());
                dto.setAiUsage(usage != null ? usage : 0L);
            }
        } else {
            dto.setName("N/A (System Admin)");
            dto.setStatus("active");
            dto.setRole("ADMIN");
            dto.setAiUsage(0L);
            dto.setRevenue(0.0);
        }
        return dto;
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