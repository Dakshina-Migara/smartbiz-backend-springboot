package com.SmartBiz.controller;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.ActivityLogDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import com.SmartBiz.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessesDto>> getAllBusinesses() {
        return new ResponseEntity<>(adminService.findAllBusinesses(), HttpStatus.OK);
    }

    @GetMapping("/businesses/search")
    public ResponseEntity<List<BusinessesDto>> searchBusinesses(@RequestParam String q) {
        return new ResponseEntity<>(adminService.searchBusinesses(q), HttpStatus.OK);
    }

    @PutMapping("/businesses/{id}/suspend")
    public ResponseEntity<BusinessesDto> suspendBusiness(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.suspendBusiness(id), HttpStatus.OK);
    }

    @PutMapping("/businesses/{id}/activate")
    public ResponseEntity<BusinessesDto> activateBusiness(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.activateBusiness(id), HttpStatus.OK);
    }

    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        adminService.deleteBusiness(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/logs/ai")
    public ResponseEntity<List<AIRequestDto>> getAiUsageLogs() {
        return new ResponseEntity<>(adminService.getGlobalAiLogs(), HttpStatus.OK);
    }

    @GetMapping("/logs/activity")
    public ResponseEntity<List<ActivityLogDto>> getActivityLogs() {
        return new ResponseEntity<>(adminService.getActivityLogs(), HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionPlanDto>> getAllPlans() {
        return new ResponseEntity<>(adminService.getAllSubscriptionPlans(), HttpStatus.OK);
    }

    @PostMapping("/subscriptions/create")
    public ResponseEntity<SubscriptionPlanDto> createPlan(@Valid @RequestBody SubscriptionPlanDto planDto) {
        return new ResponseEntity<>(adminService.createSubscriptionPlan(planDto), HttpStatus.CREATED);
    }

    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(@PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        return new ResponseEntity<>(adminService.updateSubscriptionPlan(id, planDto), HttpStatus.OK);
    }

    @DeleteMapping("/subscriptions/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        adminService.deleteSubscriptionPlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        return new ResponseEntity<>(adminService.getSystemWideStatus(), HttpStatus.OK);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return new ResponseEntity<>(adminService.getDashboardStats(), HttpStatus.OK);
    }
}