package com.SmartBiz.repository;

import com.SmartBiz.entity.Businesses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Businesses, Long> {
}