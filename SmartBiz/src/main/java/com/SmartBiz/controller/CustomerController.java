package com.SmartBiz.controller;

import com.SmartBiz.dto.CustomerDto;
import com.SmartBiz.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(@PathVariable Long businessId,
            @Valid @RequestBody CustomerDto dto) {
        return new ResponseEntity<>(customerService.addCustomer(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers(@PathVariable Long businessId) {
        return ResponseEntity.ok(customerService.getAllCustomers(businessId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@PathVariable Long businessId,
            @RequestParam String q) {
        return ResponseEntity.ok(customerService.searchCustomers(businessId, q));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long businessId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(customerService.updateCustomer(businessId, customerId, dto));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long businessId,
            @PathVariable Long customerId) {
        customerService.deleteCustomer(businessId, customerId);
        return ResponseEntity.noContent().build();
    }
}
