package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.BusinessOwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/{businessId}")
public class MobileController {

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final BusinessOwnerService businessOwnerService;

    @Autowired
    public MobileController(InventoryRepository inventoryRepository,
            SalesRepository salesRepository,
            BusinessOwnerService businessOwnerService) {
        this.inventoryRepository = inventoryRepository;
        this.salesRepository = salesRepository;
        this.businessOwnerService = businessOwnerService;
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

    // Inventory Endpoints for Mobile
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getMobileInventory(@PathVariable Long businessId) {
        return ResponseEntity.ok(businessOwnerService.getAllInventory(businessId));
    }

    @GetMapping("/inventory/search")
    public ResponseEntity<List<InventoryDto>> searchMobileInventory(@PathVariable Long businessId,
            @RequestParam String q) {
        return ResponseEntity.ok(businessOwnerService.searchInventory(businessId, q));
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addMobileInventory(@PathVariable Long businessId,
            @Valid @RequestBody InventoryDto dto) {
        dto.setBusiness_id(businessId);
        return new ResponseEntity<>(businessOwnerService.addInventory(dto), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateMobileInventory(@PathVariable Long businessId,
            @PathVariable Long productId, @Valid @RequestBody InventoryDto dto) {
        return ResponseEntity.ok(businessOwnerService.updateProduct(productId, dto, businessId));
    }

    @DeleteMapping("/inventory/{productId}")
    public ResponseEntity<Void> deleteMobileInventory(@PathVariable Long businessId, @PathVariable Long productId) {
        businessOwnerService.deleteProduct(productId, businessId);
        return ResponseEntity.noContent().build();
    }
}
