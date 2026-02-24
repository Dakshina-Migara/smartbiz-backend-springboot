package com.SmartBiz.service.impl;

import com.SmartBiz.dto.*;
import com.SmartBiz.entity.*;
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

    @Autowired
    public AdminServiceImpl(BusinessRepository businessRepository, AiRequestRepository aiRequestRepository,
            SubscriptionPlanRepository subscriptionPlanRepository) {
        this.businessRepository = businessRepository;
        this.aiRequestRepository = aiRequestRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
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
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto) {
        try {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setPlan_name(planDto.getPlan_name());
            plan.setPrice(planDto.getPrice());
            plan.setAi_token_limit(planDto.getAi_token_limit());
            plan.setMax_users(planDto.getMax_users());

            SubscriptionPlan saved = subscriptionPlanRepository.save(plan);
            log.info("Created subscription plan: {}", saved.getPlan_name());
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
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));

            plan.setPlan_name(planDto.getPlan_name());
            plan.setPrice(planDto.getPrice());
            plan.setAi_token_limit(planDto.getAi_token_limit());
            plan.setMax_users(planDto.getMax_users());

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

    private BusinessesDto mapToBusinessDto(Businesses b) {
        BusinessesDto dto = new BusinessesDto();
        dto.setBusiness_id(b.getBusiness_id());
        dto.setName(b.getName());
        dto.setAddress(b.getAddress());
        dto.setEmail(b.getEmail());
        dto.setPhone(b.getPhone());
        return dto;
    }

    private SubscriptionPlanDto mapToSubscriptionDto(SubscriptionPlan s) {
        SubscriptionPlanDto dto = new SubscriptionPlanDto();
        dto.setSubscription_id(s.getSubscription_id());
        dto.setPlan_name(s.getPlan_name());
        dto.setPrice(s.getPrice());
        dto.setAi_token_limit(s.getAi_token_limit());
        dto.setMax_users(s.getMax_users());
        dto.setCreated_at(s.getCreated_at());
        return dto;
    }

    private AIRequestDto mapToAiDto(AiRequest a) {
        AIRequestDto dto = new AIRequestDto();
        dto.setRequest_Id(a.getRequest_Id());
        dto.setPrompt(a.getPrompt());
        dto.setResponse(a.getResponse());
        dto.setTokenUsed(a.getTokenUsed());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}