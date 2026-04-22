package com.SmartBiz.controller;

import com.SmartBiz.dto.CustomerDto;
import com.SmartBiz.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerDto> addCustomer(@PathVariable @NonNull Long businessId, @Valid @RequestBody @NonNull CustomerDto dto) {
        return new ResponseEntity<>(customerService.addCustomer(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(customerService.getAllCustomers(businessId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@PathVariable @NonNull Long businessId, @RequestParam @NonNull String q) {
        return new ResponseEntity<>(customerService.searchCustomers(businessId, q), HttpStatus.OK);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long customerId,
            @Valid @RequestBody @NonNull CustomerDto dto) {
        return new ResponseEntity<>(customerService.updateCustomer(businessId, customerId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long customerId) {
        customerService.deleteCustomer(businessId, customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
