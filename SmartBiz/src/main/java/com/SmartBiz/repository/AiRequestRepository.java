package com.SmartBiz.repository;

import com.SmartBiz.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {

        @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a")
        Long sumAllTokens();

        @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a " +
                        "WHERE MONTH(a.createdAt) = MONTH(CURRENT_DATE) AND YEAR(a.createdAt) = YEAR(CURRENT_DATE)")
        Long sumTokensThisMonth();

        @Query("SELECT CAST(a.createdAt AS DATE), SUM(a.tokenUsed) FROM AiRequest a " +
                        "WHERE a.createdAt >= CURRENT_DATE - 30 " +
                        "GROUP BY CAST(a.createdAt AS DATE) ORDER BY CAST(a.createdAt AS DATE)")
        List<Object[]> dailyTokenUsageLast30Days();

        @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a WHERE a.business.business_id = :businessId")
        Long sumTokensByBusinessId(@Param("businessId") Long businessId);
}