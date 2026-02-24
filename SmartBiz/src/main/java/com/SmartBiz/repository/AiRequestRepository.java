package com.SmartBiz.repository;

import com.SmartBiz.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {

    @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a")
    Long sumAllTokens();
}