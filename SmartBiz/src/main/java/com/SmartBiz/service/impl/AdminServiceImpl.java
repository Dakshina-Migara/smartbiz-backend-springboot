package com.SmartBiz.service.impl;

import com.SmartBiz.dto.*;
import com.SmartBiz.entity.*;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final BusinessRepository businessRepository;
    private final AiRequestRepository aiRequestRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public AdminServiceImpl(BusinessRepository businessRepository, AiRequestRepository aiRequestRepository, SubscriptionPlanRepository subscriptionPlanRepository) {
        this.businessRepository = businessRepository;
        this.aiRequestRepository = aiRequestRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public List<BusinessesDto> findAllBusinesses() {
        try {
            return businessRepository.findAll().stream().map(this::mapToBusinessDto).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching businesses: " + e.getMessage());
            return new ArrayList<>();
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
            return mapToSubscriptionDto(subscriptionPlanRepository.save(plan));
        } catch (Exception e) {
            System.err.println("Error creating subscription plan: " + e.getMessage());
            return null;
        }
    }

    @Override
    public SubscriptionPlanDto updateSubscriptionPlan(Long id, SubscriptionPlanDto planDto) {
        try {
            Optional<SubscriptionPlan> optionalPlan = subscriptionPlanRepository.findById(id);
            if (optionalPlan.isPresent()) {
                SubscriptionPlan plan = optionalPlan.get();
                plan.setPlan_name(planDto.getPlan_name());
                plan.setPrice(planDto.getPrice());
                plan.setAi_token_limit(planDto.getAi_token_limit());
                plan.setMax_users(planDto.getMax_users());
                return mapToSubscriptionDto(subscriptionPlanRepository.save(plan));
            } else {
                System.err.println("Subscription plan not found with id: " + id);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error updating subscription plan: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> getSystemWideStatus() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalBusinesses", businessRepository.count());
            stats.put("totalAiRequests", aiRequestRepository.count());
            stats.put("totalTokensUsed", aiRequestRepository.sumAllTokens() != null ? aiRequestRepository.sumAllTokens() : 0);
        } catch (Exception e) {
            System.err.println("Error fetching system stats: " + e.getMessage());
            stats.put("error", "Unable to fetch system statistics");
        }
        return stats;
    }

    @Override
    public List<AIRequestDto> getGlobalAiLogs() {
        try {
            return aiRequestRepository.findAll().stream().map(this::mapToAiDto).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching AI logs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private BusinessesDto mapToBusinessDto(Businesses b) {
        BusinessesDto dto = new BusinessesDto();
        dto.setBusiness_id(b.getBusiness_id());
        dto.setName(b.getName());
        dto.setEmail(b.getEmail());
        if (b.getSubscription() != null) {
            dto.setName(b.getSubscription().getPlan_name());
        }
        return dto;
    }

    private SubscriptionPlanDto mapToSubscriptionDto(SubscriptionPlan s) {
        SubscriptionPlanDto dto = new SubscriptionPlanDto();
        dto.setSubscription_id(s.getSubscription_id());
        dto.setPlan_name(s.getPlan_name());
        dto.setPrice(s.getPrice());
        dto.setAi_token_limit(s.getAi_token_limit());
        dto.setMax_users(s.getMax_users());
        return dto;
    }

    private AIRequestDto mapToAiDto(AiRequest a) {
        AIRequestDto dto = new AIRequestDto();
        dto.setRequest_Id(a.getRequest_Id());
        dto.setPrompt(a.getPrompt());
        dto.setTokenUsed(a.getTokenUsed());
        return dto;
    }
}