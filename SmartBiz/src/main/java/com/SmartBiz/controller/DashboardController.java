package com.SmartBiz.controller;

import com.SmartBiz.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(@PathVariable Long businessId) {
        return new ResponseEntity<>(dashboardService.getKPIs(businessId), HttpStatus.OK);
    }
}
