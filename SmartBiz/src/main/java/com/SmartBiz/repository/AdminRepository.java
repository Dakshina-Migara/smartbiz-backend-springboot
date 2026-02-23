package com.SmartBiz.repository;

import com.SmartBiz.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    @Query("SELECT a FROM Admin a WHERE a.business.business_id = :business_id")
    Optional<Admin> findByBusinessId(@Param("businessId") Long business_id);

    Boolean existsByEmail(String email);
}