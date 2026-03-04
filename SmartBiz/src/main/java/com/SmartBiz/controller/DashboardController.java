package com.SmartBiz.controller;

import com.SmartBiz.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(@PathVariable Long businessId) {
        return new ResponseEntity<>(dashboardService.getKPIs(businessId), HttpStatus.OK);
    }
}
