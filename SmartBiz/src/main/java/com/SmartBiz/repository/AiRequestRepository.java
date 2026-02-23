package com.SmartBiz.repository;

import com.SmartBiz.entity.AiRequest; // Ensure this matches your entity name
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {

    // Custom query to sum all tokens for system-wide stats
    @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a")
    Long sumAllTokens();
}