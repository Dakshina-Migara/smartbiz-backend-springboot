package com.SmartBiz.controller;

import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(@PathVariable Long businessId) {
        Map<String, Object> kpis = new HashMap<>();

        var sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);
        double totalSales = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                .sum();
        kpis.put("totalSales", totalSales);
        kpis.put("totalTransactions", sales.size());

        var inventory = inventoryRepository.findByBusinessId(businessId);
        kpis.put("totalProducts", inventory.size());
        long lowStock = inventory.stream()
                .filter(i -> i.getStockLevel() != null && i.getStockLevel() < 5)
                .count();
        kpis.put("lowStockAlerts", lowStock);

        return ResponseEntity.ok(kpis);
    }
}
