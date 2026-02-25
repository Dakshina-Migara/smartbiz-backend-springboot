package com.SmartBiz.service.impl;

import com.SmartBiz.dto.TransactionDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Transaction;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.TransactionRepository;
import com.SmartBiz.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
            BusinessRepository businessRepository) {
        this.transactionRepository = transactionRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    public TransactionDto addTransaction(Long businessId, TransactionDto dto) {
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
    public List<TransactionDto> getAllTransactions(Long businessId) {
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
    public List<TransactionDto> searchTransactions(Long businessId, String query) {
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
    public List<TransactionDto> filterByType(Long businessId, String type) {
        try {
            return transactionRepository.findByBusinessIdAndType(businessId, type.toLowerCase())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error filtering transactions for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public TransactionDto updateTransaction(Long businessId, Long transactionId, TransactionDto dto) {
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
    public void deleteTransaction(Long businessId, Long transactionId) {
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
    public Map<String, Object> getTransactionSummary(Long businessId) {
        try {
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("totalIncome", transactionRepository.sumIncomeByBusinessId(businessId));
            summary.put("totalExpenses", transactionRepository.sumExpensesByBusinessId(businessId));
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
