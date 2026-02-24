package com.SmartBiz.controller;

import com.SmartBiz.dto.InvoiceDto;
import com.SmartBiz.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@PathVariable Long businessId,
            @Valid @RequestBody InvoiceDto dto) {
        return new ResponseEntity<>(invoiceService.createInvoice(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoices(@PathVariable Long businessId) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(businessId));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long businessId,
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }
}
