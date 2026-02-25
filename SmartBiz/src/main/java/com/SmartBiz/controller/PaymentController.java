package com.SmartBiz.controller;

import com.SmartBiz.dto.PaymentDto;
import com.SmartBiz.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/record")
    public ResponseEntity<PaymentDto> recordPayment(@PathVariable Long businessId,
            @Valid @RequestBody PaymentDto dto) {
        return new ResponseEntity<>(paymentService.recordPayment(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PaymentDto>> getPayments(@PathVariable Long businessId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBusiness(businessId));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsBySale(@PathVariable Long businessId,
            @PathVariable Long saleId) {
        return ResponseEntity.ok(paymentService.getPaymentsBySale(saleId));
    }
}
