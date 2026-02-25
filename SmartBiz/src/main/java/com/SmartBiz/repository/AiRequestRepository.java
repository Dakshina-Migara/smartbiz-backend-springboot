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

        @Query(value = "SELECT DATE(created_at) AS log_date, SUM(token_used) AS total_tokens " +
                        "FROM ai_request WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                        "GROUP BY DATE(created_at) ORDER BY log_date", nativeQuery = true)
        List<Object[]> dailyTokenUsageLast30Days();

        @Query("SELECT SUM(a.tokenUsed) FROM AiRequest a WHERE a.business.businessId = :businessId")
        Long sumTokensByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT a FROM AiRequest a WHERE a.business.businessId = :businessId ORDER BY a.createdAt DESC")
        List<AiRequest> findByBusinessId(@Param("businessId") Long businessId);

        @Query("SELECT a FROM AiRequest a WHERE a.business.businessId = :businessId AND a.type = :type ORDER BY a.createdAt DESC")
        List<AiRequest> findByBusinessIdAndType(@Param("businessId") Long businessId, @Param("type") String type);
}
