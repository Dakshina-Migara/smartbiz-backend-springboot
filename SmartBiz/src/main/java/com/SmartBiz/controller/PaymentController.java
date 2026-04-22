package com.SmartBiz.controller;

import com.SmartBiz.dto.PaymentDto;
import com.SmartBiz.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/record")
    public ResponseEntity<PaymentDto> recordPayment(@PathVariable @NonNull Long businessId, @Valid @RequestBody @NonNull PaymentDto dto) {
        return new ResponseEntity<>(paymentService.recordPayment(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PaymentDto>> getPayments(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(paymentService.getPaymentsByBusiness(businessId), HttpStatus.OK);
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsBySale(@PathVariable @NonNull Long businessId,
            @PathVariable @NonNull Long saleId) {
        return new ResponseEntity<>(paymentService.getPaymentsBySale(saleId), HttpStatus.OK);
    }
}
