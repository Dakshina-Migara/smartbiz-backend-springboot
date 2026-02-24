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

/**
 * BusinessOwnerController - REST API Controller for Business Owner operations.
 *
 * Available Endpoints:
 * POST /api/v1/business/inventory → Add a new product
 * GET /api/v1/business/{businessId}/inventory → List all products
 * PUT /api/v1/business/{businessId}/inventory/{productId} → Update stock level
 * POST /api/v1/business/sales → Record a sale
 * GET /api/v1/business/{businessId}/sales → View sales history
 * POST /api/v1/business/{businessId}/ai-insight → Generate AI insight
 *
 * Now uses ResponseEntity for consistent HTTP status codes (matching
 * AdminController).
 * Added @Valid for DTO validation on POST/PUT endpoints.
 */
@RestController
@RequestMapping("/api/v1/business")
public class BusinessOwnerController {

    private final BusinessOwnerService service;

    @Autowired
    public BusinessOwnerController(BusinessOwnerService service) {
        this.service = service;
    }

    /**
     * POST /api/v1/business/inventory — Add a new product.
     * 
     * @Valid triggers DTO validation (checks @NotBlank, @NotNull, @Min).
     *        Returns HTTP 201 CREATED on success.
     */
    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody InventoryDto dto) {
        InventoryDto created = service.addInventory(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/business/{businessId}/inventory — List all products for a
     * business.
     * Returns HTTP 200 OK with the inventory list.
     */
    @GetMapping("/{businessId}/inventory")
    public ResponseEntity<List<InventoryDto>> getAllInventory(@PathVariable Long businessId) {
        return ResponseEntity.ok(service.getAllInventory(businessId));
    }

    /**
     * PUT /api/v1/business/{businessId}/inventory/{productId}?quantity=10
     * Updates stock level. Quantity is passed as a query parameter.
     * Returns HTTP 200 OK with the updated product.
     */
    @PutMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateStock(@PathVariable Long businessId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(service.updateStock(productId, quantity, businessId));
    }

    /**
     * POST /api/v1/business/sales — Record a new sale.
     * 
     * @Valid triggers DTO validation.
     *        Returns HTTP 201 CREATED on success.
     */
    @PostMapping("/sales")
    public ResponseEntity<SalesDto> recordSale(@Valid @RequestBody SalesDto dto) {
        SalesDto created = service.recordSale(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/business/{businessId}/sales — View sales history.
     * Returns HTTP 200 OK with sales sorted by date (newest first).
     */
    @GetMapping("/{businessId}/sales")
    public ResponseEntity<List<SalesDto>> getSalesHistory(@PathVariable Long businessId) {
        return ResponseEntity.ok(service.getSalesHistory(businessId));
    }

    /**
     * DELETE /api/v1/business/{businessId}/inventory/{productId} — Delete a
     * product.
     * Returns HTTP 204 No Content on success.
     */
    @DeleteMapping("/{businessId}/inventory/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long businessId,
            @PathVariable Long productId) {
        service.deleteProduct(productId, businessId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/business/{businessId}/ai-insight — Generate AI insight.
     * Returns HTTP 200 OK with the AI-generated insight string.
     */
    @PostMapping("/{businessId}/ai-insight")
    public ResponseEntity<String> generateAiInsight(@PathVariable Long businessId,
            @RequestBody String prompt) {
        return ResponseEntity.ok(service.generateAiInsight(businessId, prompt));
    }
}