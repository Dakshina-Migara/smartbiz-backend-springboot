package com.SmartBiz.controller;

import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/{businessId}")
public class MobileController {

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;

    @Autowired
    public MobileController(InventoryRepository inventoryRepository, SalesRepository salesRepository) {
        this.inventoryRepository = inventoryRepository;
        this.salesRepository = salesRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMobileDashboard(@PathVariable Long businessId) {
        Map<String, Object> data = new LinkedHashMap<>();

        // Stats for the 4 cards
        long totalProducts = inventoryRepository.findByBusinessId(businessId).size();
        var sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);
        long totalSales = sales.size();
        double revenue = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                .sum();
        long lowStockItems = inventoryRepository.countLowStock(businessId);

        data.put("totalProducts", totalProducts);
        data.put("totalSales", totalSales);
        data.put("revenue", revenue);
        data.put("lowStockItems", lowStockItems);

        return ResponseEntity.ok(data);
    }
}
