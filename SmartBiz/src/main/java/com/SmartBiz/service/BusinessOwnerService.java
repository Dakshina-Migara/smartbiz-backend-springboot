package com.SmartBiz.service;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public interface BusinessOwnerService {

    InventoryDto addInventory(@NonNull InventoryDto dto);

    List<InventoryDto> getAllInventory(@NonNull Long businessId);

    List<InventoryDto> searchInventory(@NonNull Long businessId, @NonNull String query);

    List<InventoryDto> filterInventoryByStatus(@NonNull Long businessId, @NonNull String status);

    InventoryDto updateStock(@NonNull Long productId, @NonNull Integer quantity, @NonNull Long businessId);

    InventoryDto adjustStock(@NonNull Long productId, int adjustment, @NonNull Long businessId);

    InventoryDto updateProduct(@NonNull Long productId, @NonNull InventoryDto dto, @NonNull Long businessId);

    Map<String, Object> getInventoryStats(@NonNull Long businessId);

    SalesDto recordSale(@NonNull SalesDto dto);

    List<SalesDto> getSalesHistory(@NonNull Long businessId);

    List<SalesDto> searchSales(@NonNull Long businessId, @NonNull String query);

    void deleteProduct(@NonNull Long productId, @NonNull Long businessId);

    String generateAiInsight(@NonNull Long businessId, @NonNull String prompt);

    SalesDto recordMobileSale(@NonNull Long businessId, @NonNull com.SmartBiz.dto.MobileSaleRequestDto dto);

    SalesDto getSaleById(@NonNull Long businessId, @NonNull Long saleId);

    void deleteSale(@NonNull Long businessId, @NonNull Long saleId);

    void subscribeToPlan(@NonNull Long businessId, @NonNull Long planId);
}