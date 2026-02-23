package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.service.BusinessOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owner")
public class BusinessOwnerController {

    private final BusinessOwnerService businessOwnerService;

    @Autowired
    public BusinessOwnerController(BusinessOwnerService businessOwnerService) {
        this.businessOwnerService = businessOwnerService;
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addProduct(@RequestBody InventoryDto dto) {
        InventoryDto saved = businessOwnerService.addInventory(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getInventory(@RequestParam Long business_id) {
        List<InventoryDto> inventory = businessOwnerService.getAllInventory(business_id);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/sales")
    public ResponseEntity<SalesDto> recordSale(@RequestBody SalesDto dto) {
        SalesDto savedSale = businessOwnerService.recordSale(dto);
        return ResponseEntity.ok(savedSale);
    }

    @PostMapping("/ai/insight")
    public ResponseEntity<String> getAiInsight(
            @RequestParam Long business_id,
            @RequestBody String prompt) {
        String insight = businessOwnerService.generateAiInsight(business_id, prompt);
        return ResponseEntity.ok(insight);
    }
}