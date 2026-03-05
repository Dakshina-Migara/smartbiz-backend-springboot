package com.SmartBiz.service;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;

import java.util.List;
import java.util.Map;

public interface BusinessOwnerService {

    InventoryDto addInventory(InventoryDto dto);

    List<InventoryDto> getAllInventory(Long businessId);

    List<InventoryDto> searchInventory(Long businessId, String query);

    List<InventoryDto> filterInventoryByStatus(Long businessId, String status);

    InventoryDto updateStock(Long productId, Integer quantity, Long businessId);

    InventoryDto adjustStock(Long productId, int adjustment, Long businessId);

    InventoryDto updateProduct(Long productId, InventoryDto dto, Long businessId);

    Map<String, Object> getInventoryStats(Long businessId);

    SalesDto recordSale(SalesDto dto);

    List<SalesDto> getSalesHistory(Long businessId);

    List<SalesDto> searchSales(Long businessId, String query);

    void deleteProduct(Long productId, Long businessId);

    String generateAiInsight(Long businessId, String prompt);

    SalesDto recordMobileSale(Long businessId, com.SmartBiz.dto.MobileSaleRequestDto dto);

    SalesDto getSaleById(Long businessId, Long saleId);
}