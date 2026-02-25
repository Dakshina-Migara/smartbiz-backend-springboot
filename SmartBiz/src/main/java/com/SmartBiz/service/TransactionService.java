package com.SmartBiz.service;

import com.SmartBiz.dto.TransactionDto;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    TransactionDto addTransaction(Long businessId, TransactionDto dto);

    List<TransactionDto> getAllTransactions(Long businessId);

    List<TransactionDto> searchTransactions(Long businessId, String query);

    List<TransactionDto> filterByType(Long businessId, String type);

    TransactionDto updateTransaction(Long businessId, Long transactionId, TransactionDto dto);

    void deleteTransaction(Long businessId, Long transactionId);

    Map<String, Object> getTransactionSummary(Long businessId);
}
