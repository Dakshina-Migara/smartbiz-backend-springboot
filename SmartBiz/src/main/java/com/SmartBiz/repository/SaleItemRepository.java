package com.SmartBiz.repository;

import com.SmartBiz.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("SELECT si FROM SaleItem si WHERE si.sale.saleId = :saleId")
    List<SaleItem> findBySaleId(@Param("saleId") Long saleId);
}
