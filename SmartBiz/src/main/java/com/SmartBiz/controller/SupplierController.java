package com.SmartBiz.controller;

import com.SmartBiz.dto.SupplierDto;
import com.SmartBiz.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/addSupplier")
    public ResponseEntity<SupplierDto> addSupplier(@PathVariable @NonNull Long businessId, @Valid @RequestBody @NonNull SupplierDto dto) {
        return new ResponseEntity<>(supplierService.addSupplier(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<List<SupplierDto>> getAllSuppliers(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(supplierService.getAllSuppliers(businessId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDto>> searchSuppliers(@PathVariable @NonNull Long businessId, @RequestParam @NonNull String q) {
        return new ResponseEntity<>(supplierService.searchSuppliers(businessId, q), HttpStatus.OK);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long supplierId,
            @Valid @RequestBody @NonNull SupplierDto dto) {
        return new ResponseEntity<>(supplierService.updateSupplier(businessId, supplierId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long supplierId) {
        supplierService.deleteSupplier(businessId, supplierId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
