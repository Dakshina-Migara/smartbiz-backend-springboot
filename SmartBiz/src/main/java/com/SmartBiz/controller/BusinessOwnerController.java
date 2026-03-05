package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.service.BusinessOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business")
@RequiredArgsConstructor
public class BusinessOwnerController {

    private final BusinessOwnerService service;

    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody InventoryDto dto) {
        return new ResponseEntity<>(service.addInventory(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{businessId}/inventory")
    public ResponseEntity<List<InventoryDto>> getAllInventory(@PathVariable Long businessId) {
        return new ResponseEntity<>(service.getAllInventory(businessId), HttpStatus.OK);
    }

    @GetMapping("/{businessId}/inventory/search")
    public ResponseEntity<List<InventoryDto>> searchInventory(@PathVariable Long businessId, @RequestParam String q) {
        return new ResponseEntity<>(service.searchInventory(businessId, q), HttpStatus.OK);
    }

    @GetMapping("/{businessId}/inventory/filter")
    public ResponseEntity<List<InventoryDto>> filterInventory(@PathVariable Long businessId,
            @RequestParam String status) {
        return new ResponseEntity<>(service.filterInventoryByStatus(businessId, status), HttpStatus.OK);
    }

    @GetMapping("/{businessId}/inventory/stats")
    public ResponseEntity<Map<String, Object>> getInventoryStats(@PathVariable Long businessId) {
        return new ResponseEntity<>(service.getInventoryStats(businessId), HttpStatus.OK);
    }

    @PutMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateStock(@PathVariable Long businessId, @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return new ResponseEntity<>(service.updateStock(productId, quantity, businessId), HttpStatus.OK);
    }

    @PutMapping("/{businessId}/inventory/{productId}/edit")
    public ResponseEntity<InventoryDto> updateProduct(@PathVariable Long businessId, @PathVariable Long productId,
            @Valid @RequestBody InventoryDto dto) {
        return new ResponseEntity<>(service.updateProduct(productId, dto, businessId), HttpStatus.OK);
    }

    @PostMapping("/sales")
    public ResponseEntity<SalesDto> recordSale(@Valid @RequestBody SalesDto dto) {
        return new ResponseEntity<>(service.recordSale(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{businessId}/sales")
    public ResponseEntity<List<SalesDto>> getSalesHistory(@PathVariable Long businessId) {
        return new ResponseEntity<>(service.getSalesHistory(businessId), HttpStatus.OK);
    }

    @GetMapping("/{businessId}/sales/search")
    public ResponseEntity<List<SalesDto>> searchSales(@PathVariable Long businessId, @RequestParam String q) {
        return new ResponseEntity<>(service.searchSales(businessId, q), HttpStatus.OK);
    }

    @GetMapping("/{businessId}/sales/{saleId}")
    public ResponseEntity<SalesDto> getSaleDetails(@PathVariable Long businessId, @PathVariable Long saleId) {
        return new ResponseEntity<>(service.getSaleById(businessId, saleId), HttpStatus.OK);
    }

    @DeleteMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long businessId, @PathVariable Long productId) {
        service.deleteProduct(productId, businessId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{businessId}/sales/{saleId}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long businessId, @PathVariable Long saleId) {
        service.deleteSale(businessId, saleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{businessId}/ai-insight")
    public ResponseEntity<String> generateAiInsight(@PathVariable Long businessId, @RequestBody String prompt) {
        return new ResponseEntity<>(service.generateAiInsight(businessId, prompt), HttpStatus.OK);
    }
}