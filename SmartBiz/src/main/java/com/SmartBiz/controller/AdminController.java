package com.SmartBiz.controller;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import com.SmartBiz.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessesDto>> getAllBusinesses() {
        List<BusinessesDto> businesses = adminService.findAllBusinesses();
        return ResponseEntity.ok(businesses);
    }

    @GetMapping("/logs/ai")
    public ResponseEntity<List<AIRequestDto>> getAiUsageLogs() {
        List<AIRequestDto> logs = adminService.getGlobalAiLogs();
        return ResponseEntity.ok(logs);
    }

    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {

        SubscriptionPlanDto updatedPlan = adminService.updateSubscriptionPlan(id, planDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @PostMapping("/subscriptions/create")
    public ResponseEntity<SubscriptionPlanDto> createPlan(
            @Valid @RequestBody SubscriptionPlanDto planDto) {

        SubscriptionPlanDto createdPlan = adminService.createSubscriptionPlan(planDto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> statistics = adminService.getSystemWideStatus();
        return ResponseEntity.ok(statistics);
    }
}