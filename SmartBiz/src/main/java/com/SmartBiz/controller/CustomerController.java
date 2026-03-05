package com.SmartBiz.controller;

import com.SmartBiz.dto.CustomerDto;
import com.SmartBiz.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerDto> addCustomer(@PathVariable Long businessId, @Valid @RequestBody CustomerDto dto) {
        return new ResponseEntity<>(customerService.addCustomer(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(@PathVariable Long businessId) {
        return new ResponseEntity<>(customerService.getAllCustomers(businessId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@PathVariable Long businessId, @RequestParam String q) {
        return new ResponseEntity<>(customerService.searchCustomers(businessId, q), HttpStatus.OK);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long businessId, @PathVariable Long customerId,
            @Valid @RequestBody CustomerDto dto) {
        return new ResponseEntity<>(customerService.updateCustomer(businessId, customerId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long businessId, @PathVariable Long customerId) {
        customerService.deleteCustomer(businessId, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
