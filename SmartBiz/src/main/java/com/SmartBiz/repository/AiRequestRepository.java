package com.SmartBiz.repository;

import com.SmartBiz.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * AiRequestRepository - Data Access Layer for the AiRequest entity.
 *
 * Extends JpaRepository<AiRequest, Long> which provides built-in CRUD methods:
 * - save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * @Repository marks this as a Spring-managed data access bean.
 */
@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {

    /**
     * Custom JPQL query to calculate the total number of AI tokens used
     * across ALL AI requests in the entire system.
     *
     * JPQL: "SELECT SUM(a.tokenUsed) FROM AiRequest a"
     * → Sums the "tokenUsed" field from every AiRequest record
     *
     * Used in AdminServiceImpl.getSystemWideStatus() to display
     * system-wide statistics on the admin dashboard.
     *
     * Returns null if there are no AI requests in the database,
     * so the service layer handles null checks.
     *
     * @return total tokens used across all requests, or null if no records exist
     */
    @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a")
    Long sumAllTokens();
}