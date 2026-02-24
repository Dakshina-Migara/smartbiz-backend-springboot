package com.SmartBiz.controller;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.ActivityLogDto;
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

    @GetMapping("/businesses/search")
    public ResponseEntity<List<BusinessesDto>> searchBusinesses(@RequestParam String q) {
        List<BusinessesDto> results = adminService.searchBusinesses(q);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/businesses/{id}/suspend")
    public ResponseEntity<BusinessesDto> suspendBusiness(@PathVariable Long id) {
        BusinessesDto suspended = adminService.suspendBusiness(id);
        return ResponseEntity.ok(suspended);
    }

    @PutMapping("/businesses/{id}/activate")
    public ResponseEntity<BusinessesDto> activateBusiness(@PathVariable Long id) {
        BusinessesDto activated = adminService.activateBusiness(id);
        return ResponseEntity.ok(activated);
    }

    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        adminService.deleteBusiness(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs/ai")
    public ResponseEntity<List<AIRequestDto>> getAiUsageLogs() {
        List<AIRequestDto> logs = adminService.getGlobalAiLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/activity")
    public ResponseEntity<List<ActivityLogDto>> getActivityLogs() {
        List<ActivityLogDto> logs = adminService.getActivityLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        List<SubscriptionPlanDto> plans = adminService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/subscriptions/create")
    public ResponseEntity<SubscriptionPlanDto> createPlan(
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto createdPlan = adminService.createSubscriptionPlan(planDto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto updatedPlan = adminService.updateSubscriptionPlan(id, planDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        adminService.deleteSubscriptionPlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> statistics = adminService.getSystemWideStatus();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> dashboard = adminService.getDashboardStats();
        return ResponseEntity.ok(dashboard);
    }
}