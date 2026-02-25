package com.SmartBiz.repository;

import com.SmartBiz.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.business.business_id = :businessId ORDER BY t.date DESC")
    List<Transaction> findByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.business.business_id = :businessId AND t.type = 'income'")
    Double sumIncomeByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.business.business_id = :businessId AND t.type = 'expense'")
    Double sumExpensesByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT t FROM Transaction t WHERE t.business.business_id = :businessId " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Transaction> searchByBusinessId(@Param("businessId") Long businessId, @Param("query") String query);

    @Query("SELECT t FROM Transaction t WHERE t.business.business_id = :businessId AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findByBusinessIdAndType(@Param("businessId") Long businessId, @Param("type") String type);
}
