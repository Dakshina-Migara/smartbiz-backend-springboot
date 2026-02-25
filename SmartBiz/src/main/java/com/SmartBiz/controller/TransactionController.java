package com.SmartBiz.controller;

import com.SmartBiz.dto.TransactionDto;
import com.SmartBiz.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> addTransaction(@PathVariable Long businessId,
            @Valid @RequestBody TransactionDto dto) {
        return new ResponseEntity<>(transactionService.addTransaction(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable Long businessId) {
        return ResponseEntity.ok(transactionService.getAllTransactions(businessId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDto>> searchTransactions(@PathVariable Long businessId,
            @RequestParam String q) {
        return ResponseEntity.ok(transactionService.searchTransactions(businessId, q));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDto>> filterByType(@PathVariable Long businessId,
            @RequestParam String type) {
        return ResponseEntity.ok(transactionService.filterByType(businessId, type));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(@PathVariable Long businessId) {
        return ResponseEntity.ok(transactionService.getTransactionSummary(businessId));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long businessId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDto dto) {
        return ResponseEntity.ok(transactionService.updateTransaction(businessId, transactionId, dto));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long businessId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(businessId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
