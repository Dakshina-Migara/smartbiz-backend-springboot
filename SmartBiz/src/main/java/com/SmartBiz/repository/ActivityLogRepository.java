package com.SmartBiz.repository;

import com.SmartBiz.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM ActivityLog l WHERE l.business.business_id = :businessId")
    List<ActivityLog> findByBusinessId(@Param("businessId") Long businessId);
}
