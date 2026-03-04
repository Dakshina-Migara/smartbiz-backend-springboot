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

    @PostMapping("/addTransaction")
    public ResponseEntity<TransactionDto> addTransaction(@PathVariable Long businessId,
            @Valid @RequestBody TransactionDto dto) {
        return new ResponseEntity<>(transactionService.addTransaction(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@PathVariable Long businessId) {
        return new ResponseEntity<>(transactionService.getAllTransactions(businessId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionDto>> searchTransactions(@PathVariable Long businessId,
            @RequestParam String q) {
        return new ResponseEntity<>(transactionService.searchTransactions(businessId, q), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionDto>> filterByType(@PathVariable Long businessId, @RequestParam String type) {
        return new ResponseEntity<>(transactionService.filterByType(businessId, type), HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(@PathVariable Long businessId) {
        return new ResponseEntity<>(transactionService.getTransactionSummary(businessId), HttpStatus.OK);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long businessId,
            @PathVariable Long transactionId, @Valid @RequestBody TransactionDto dto) {
        return new ResponseEntity<>(transactionService.updateTransaction(businessId, transactionId, dto),
                HttpStatus.OK);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long businessId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(businessId, transactionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
