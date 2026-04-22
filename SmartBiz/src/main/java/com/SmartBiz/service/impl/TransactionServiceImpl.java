package com.SmartBiz.service.impl;

import com.SmartBiz.dto.TransactionDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Transaction;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.TransactionRepository;
import com.SmartBiz.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final BusinessRepository businessRepository;

    @Override
    public TransactionDto addTransaction(@NonNull Long businessId, @NonNull TransactionDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            Transaction transaction = new Transaction();
            transaction.setType(dto.getType());
            transaction.setCategory(dto.getCategory());
            transaction.setDescription(dto.getDescription());
            transaction.setAmount(dto.getAmount());
            transaction.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
            transaction.setBusiness(business);

            Transaction saved = transactionRepository.save(transaction);
            log.info("Added {} transaction for business id: {}, amount: {}", dto.getType(), businessId,
                    dto.getAmount());
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error adding transaction for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to add transaction: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions(@NonNull Long businessId) {
        try {
            return transactionRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching transactions for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> searchTransactions(@NonNull Long businessId, @NonNull String query) {
        try {
            return transactionRepository.searchByBusinessId(businessId, query)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching transactions for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> filterByType(@NonNull Long businessId, @NonNull String type) {
        try {
            return transactionRepository.findByBusinessIdAndType(businessId, type.toLowerCase())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error filtering transactions for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public TransactionDto updateTransaction(@NonNull Long businessId, @NonNull Long transactionId, @NonNull TransactionDto dto) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

            if (!transaction.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Transaction does not belong to this business");
            }

            transaction.setType(dto.getType());
            transaction.setCategory(dto.getCategory());
            transaction.setDescription(dto.getDescription());
            transaction.setAmount(dto.getAmount());
            if (dto.getDate() != null) {
                transaction.setDate(dto.getDate());
            }

            Transaction updated = transactionRepository.save(transaction);
            log.info("Updated transaction id: {}", transactionId);
            return mapToDto(updated);
        } catch (Exception e) {
            log.error("Error updating transaction id {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to update transaction: " + e.getMessage());
        }
    }

    @Override
    public void deleteTransaction(@NonNull Long businessId, @NonNull Long transactionId) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

            if (!transaction.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Transaction does not belong to this business");
            }

            transactionRepository.delete(transaction);
            log.info("Deleted transaction id: {}", transactionId);
        } catch (Exception e) {
            log.error("Error deleting transaction id {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete transaction: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTransactionSummary(@NonNull Long businessId) {
        try {
            Map<String, Object> summary = new LinkedHashMap<>();
            Double income = transactionRepository.sumIncomeByBusinessId(businessId);
            Double expenses = transactionRepository.sumExpensesByBusinessId(businessId);

            double incomeVal = income != null ? income : 0.0;
            double expensesVal = expenses != null ? expenses : 0.0;

            summary.put("totalIncome", incomeVal);
            summary.put("totalExpenses", expensesVal);
            summary.put("balance", incomeVal - expensesVal);
            return summary;
        } catch (Exception e) {
            log.error("Error fetching transaction summary for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get transaction summary: " + e.getMessage());
        }
    }

    private TransactionDto mapToDto(Transaction t) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(t.getTransactionId());
        dto.setType(t.getType());
        dto.setCategory(t.getCategory());
        dto.setDescription(t.getDescription());
        dto.setAmount(t.getAmount());
        dto.setDate(t.getDate());
        dto.setBusinessId(t.getBusiness().getBusinessId());
        return dto;
    }
}
