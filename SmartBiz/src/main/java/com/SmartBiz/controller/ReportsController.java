package com.SmartBiz.controller;

import com.SmartBiz.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/sales-trend")
    public ResponseEntity<List<Map<String, Object>>> getSalesTrend(@PathVariable Long businessId) {
        return ResponseEntity.ok(reportsService.getSalesTrend(businessId));
    }

    @GetMapping("/monthly-overview")
    public ResponseEntity<Map<String, Object>> getMonthlyOverview(@PathVariable Long businessId) {
        return ResponseEntity.ok(reportsService.getMonthlyOverview(businessId));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@PathVariable Long businessId) {
        return ResponseEntity.ok(reportsService.getTopSellingProducts(businessId));
    }

    @GetMapping("/expenses-by-category")
    public ResponseEntity<List<Map<String, Object>>> getExpensesByCategory(@PathVariable Long businessId) {
        return ResponseEntity.ok(reportsService.getExpensesByCategory(businessId));
    }

    @GetMapping("/low-stock-alerts")
    public ResponseEntity<List<Map<String, Object>>> getLowStockAlerts(@PathVariable Long businessId) {
        return ResponseEntity.ok(reportsService.getLowStockAlerts(businessId));
    }
}
