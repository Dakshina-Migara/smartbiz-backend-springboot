package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.service.BusinessOwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody InventoryDto dto) {
        InventoryDto created = service.addInventory(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{businessId}/inventory")
    public ResponseEntity<List<InventoryDto>> getAllInventory(@PathVariable Long businessId) {
        return ResponseEntity.ok(service.getAllInventory(businessId));
    }

    @GetMapping("/{businessId}/inventory/search")
    public ResponseEntity<List<InventoryDto>> searchInventory(@PathVariable Long businessId,
            @RequestParam String q) {
        return ResponseEntity.ok(service.searchInventory(businessId, q));
    }

    @PutMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateStock(@PathVariable Long businessId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.updateStock(productId, quantity, businessId));
    }

    @PutMapping("/{businessId}/inventory/{productId}/edit")
    public ResponseEntity<InventoryDto> updateProduct(@PathVariable Long businessId,
            @PathVariable Long productId,
            @Valid @RequestBody InventoryDto dto) {
        return ResponseEntity.ok(service.updateProduct(productId, dto, businessId));
    }

    @PostMapping("/sales")
    public ResponseEntity<SalesDto> recordSale(@Valid @RequestBody SalesDto dto) {
        SalesDto created = service.recordSale(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{businessId}/sales")
    public ResponseEntity<List<SalesDto>> getSalesHistory(@PathVariable Long businessId) {
        return ResponseEntity.ok(service.getSalesHistory(businessId));
    }

    @DeleteMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long businessId,
            @PathVariable Long productId) {
        service.deleteProduct(productId, businessId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{businessId}/ai-insight")
    public ResponseEntity<String> generateAiInsight(@PathVariable Long businessId,
            @RequestBody String prompt) {
        return ResponseEntity.ok(service.generateAiInsight(businessId, prompt));
    }
}