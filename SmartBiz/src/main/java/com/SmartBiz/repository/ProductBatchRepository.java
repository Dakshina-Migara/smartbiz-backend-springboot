package com.SmartBiz.repository;

import com.SmartBiz.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    @Query("SELECT pb FROM ProductBatch pb WHERE pb.product.productId = :productId")
    List<ProductBatch> findByProductId(@Param("productId") Long productId);
}
