package com.SmartBiz.controller;

import com.SmartBiz.dto.SupplierDto;
import com.SmartBiz.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping("/addSupplier")
    public ResponseEntity<SupplierDto> addSupplier(@PathVariable Long businessId,
            @Valid @RequestBody SupplierDto dto) {
        return new ResponseEntity<>(supplierService.addSupplier(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<List<SupplierDto>> getAllSuppliers(@PathVariable Long businessId) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(businessId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDto>> searchSuppliers(@PathVariable Long businessId,
            @RequestParam String q) {
        return ResponseEntity.ok(supplierService.searchSuppliers(businessId, q));
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable Long businessId,
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierDto dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(businessId, supplierId, dto));
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long businessId,
            @PathVariable Long supplierId) {
        supplierService.deleteSupplier(businessId, supplierId);
        return ResponseEntity.noContent().build();
    }
}
