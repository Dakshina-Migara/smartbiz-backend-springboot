package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.service.BusinessOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business")
public class BusinessOwnerController {

    private final BusinessOwnerService service;

    @Autowired
    public BusinessOwnerController(BusinessOwnerService service) {
        this.service = service;
    }

    @PostMapping("/inventory")
    public InventoryDto addInventory(@RequestBody InventoryDto dto) {
        return service.addInventory(dto);
    }

    @GetMapping("/{businessId}/inventory")
    public List<InventoryDto> getAllInventory(@PathVariable Long businessId) {
        return service.getAllInventory(businessId);
    }

    @PutMapping("/{businessId}/inventory/{productId}")
    public InventoryDto updateStock(@PathVariable Long businessId,
                                    @PathVariable Long productId,
                                    @RequestParam Integer quantity) {
        return service.updateStock(productId, quantity, businessId);
    }

    @PostMapping("/sales")
    public SalesDto recordSale(@RequestBody SalesDto dto) {
        return service.recordSale(dto);
    }

    @GetMapping("/{businessId}/sales")
    public List<SalesDto> getSalesHistory(@PathVariable Long businessId) {
        return service.getSalesHistory(businessId);
    }

    @PostMapping("/{businessId}/ai-insight")
    public String generateAiInsight(@PathVariable Long businessId,
                                    @RequestBody String prompt) {
        return service.generateAiInsight(businessId, prompt);
    }
}