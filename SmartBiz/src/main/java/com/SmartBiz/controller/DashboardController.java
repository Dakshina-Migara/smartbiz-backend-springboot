package com.SmartBiz.controller;

import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * DashboardController - Provides KPI data for the Business Owner Dashboard.
 *
 * Endpoints:
 * GET /api/v1/business/{businessId}/dashboard/kpis → KPI summary
 */
@RestController
@RequestMapping("/api/v1/business/{businessId}/dashboard")
public class DashboardController {

    private final SalesRepository salesRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public DashboardController(SalesRepository salesRepository, InventoryRepository inventoryRepository) {
        this.salesRepository = salesRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * GET /api/v1/business/{businessId}/dashboard/kpis
     * Returns: totalSales, totalProducts, lowStockCount
     */
    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(@PathVariable Long businessId) {
        Map<String, Object> kpis = new HashMap<>();

        // Total sales amount for this business
        var sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);
        double totalSales = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                .sum();
        kpis.put("totalSales", totalSales);
        kpis.put("totalTransactions", sales.size());

        // Inventory stats
        var inventory = inventoryRepository.findByBusinessId(businessId);
        kpis.put("totalProducts", inventory.size());
        long lowStock = inventory.stream()
                .filter(i -> i.getStockLevel() != null && i.getStockLevel() < 5)
                .count();
        kpis.put("lowStockAlerts", lowStock);

        return ResponseEntity.ok(kpis);
    }
}
