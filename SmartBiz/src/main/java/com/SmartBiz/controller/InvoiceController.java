package com.SmartBiz.controller;

import com.SmartBiz.dto.InvoiceDto;
import com.SmartBiz.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/create")
    public ResponseEntity<InvoiceDto> createInvoice(@PathVariable @NonNull Long businessId, @Valid @RequestBody @NonNull InvoiceDto dto) {
        return new ResponseEntity<>(invoiceService.createInvoice(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAllInvoices")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(invoiceService.getAllInvoices(businessId), HttpStatus.OK);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long invoiceId) {
        return new ResponseEntity<>(invoiceService.getInvoiceById(businessId, invoiceId), HttpStatus.OK);
    }
}
