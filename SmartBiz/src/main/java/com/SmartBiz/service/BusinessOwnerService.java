package com.SmartBiz.service;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;

import java.util.List;

public interface BusinessOwnerService {

    InventoryDto addInventory(InventoryDto dto);

    List<InventoryDto> getAllInventory(Long businessId);

    InventoryDto updateStock(Long productId, Integer quantity, Long businessId);

    SalesDto recordSale(SalesDto dto);

    List<SalesDto> getSalesHistory(Long businessId);

    void deleteProduct(Long productId, Long businessId);

    String generateAiInsight(Long businessId, String prompt);
}