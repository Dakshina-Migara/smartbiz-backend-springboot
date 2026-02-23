package com.SmartBiz.controller;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import com.SmartBiz.service.AdminService;
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

    // 1. Get all registered businesses
    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessesDto>> getAllBusinesses() {

        List<BusinessesDto> businesses = adminService.findAllBusinesses();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }

    // 2. View system-wide AI usage logs
    @GetMapping("/logs/ai")
    public ResponseEntity<List<AIRequestDto>> getAiUsageLogs() {

        List<AIRequestDto> logs = adminService.getGlobalAiLogs();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    // 3. Update subscription plan
    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanDto planDto) {

        SubscriptionPlanDto updatedPlan = adminService.updateSubscriptionPlan(id, planDto);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    // 4. System-wide statistics (dashboard)
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStats() {

        Map<String, Object> statistics = adminService.getSystemWideStatus();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}