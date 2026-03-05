package com.SmartBiz.controller;

import com.SmartBiz.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    @GetMapping("/sales-trend")
    public ResponseEntity<List<Map<String, Object>>> getSalesTrend(@PathVariable Long businessId) {
        return new ResponseEntity<>(reportsService.getSalesTrend(businessId), HttpStatus.OK);
    }

    @GetMapping("/monthly-overview")
    public ResponseEntity<Map<String, Object>> getMonthlyOverview(@PathVariable Long businessId) {
        return new ResponseEntity<>(reportsService.getMonthlyOverview(businessId), HttpStatus.OK);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@PathVariable Long businessId) {
        return new ResponseEntity<>(reportsService.getTopSellingProducts(businessId), HttpStatus.OK);
    }

    @GetMapping("/expenses-by-category")
    public ResponseEntity<List<Map<String, Object>>> getExpensesByCategory(@PathVariable Long businessId) {
        return new ResponseEntity<>(reportsService.getExpensesByCategory(businessId), HttpStatus.OK);
    }

    @GetMapping("/low-stock-alerts")
    public ResponseEntity<List<Map<String, Object>>> getLowStockAlerts(@PathVariable Long businessId) {
        return new ResponseEntity<>(reportsService.getLowStockAlerts(businessId), HttpStatus.OK);
    }
}
