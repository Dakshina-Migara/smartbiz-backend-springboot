package com.SmartBiz.service.impl;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Inventory;
import com.SmartBiz.entity.Sales;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.BusinessOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessOwnerServiceImpl implements BusinessOwnerService {

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public BusinessOwnerServiceImpl(InventoryRepository inventoryRepository,
                                    SalesRepository salesRepository,
                                    BusinessRepository businessRepository) {
        this.inventoryRepository = inventoryRepository;
        this.salesRepository = salesRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    public InventoryDto addInventory(InventoryDto dto) {
        Businesses business = businessRepository.findById(dto.getBusiness_id())
                .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusiness_id()));

        Inventory inventory = new Inventory();
        inventory.setProductName(dto.getProductName());
        inventory.setStockLevel(dto.getStockLevel());
        inventory.setPrice(dto.getPrice());
        inventory.setBusiness(business);

        Inventory saved = inventoryRepository.save(inventory);
        return mapToInventoryDto(saved);
    }

    @Override
    public List<InventoryDto> getAllInventory(Long businessId) {
        return inventoryRepository.findByBusinessId(businessId)
                .stream()
                .map(this::mapToInventoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateStock(Long productId, Integer quantity, Long businessId) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + productId));

        if (!inventory.getBusiness().getBusiness_id().equals(businessId)) {
            throw new RuntimeException("Unauthorized: Business mismatch");
        }

        inventory.setStockLevel(quantity);
        Inventory updated = inventoryRepository.save(inventory);
        return mapToInventoryDto(updated);
    }

    @Override
    public SalesDto recordSale(SalesDto dto) {
        Businesses business = businessRepository.findById(dto.getBusiness_id())
                .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusiness_id()));

        Sales sale = new Sales();
        sale.setTotalAmount(dto.getTotalAmount());
        sale.setItemsCount(dto.getItemsCount());
        sale.setSaleDate(dto.getSaleDate() != null ? dto.getSaleDate() : LocalDateTime.now());
        sale.setBusiness(business);

        Sales savedSale = salesRepository.save(sale);
        return mapToSalesDto(savedSale);
    }

    @Override
    public List<SalesDto> getSalesHistory(Long businessId) {
        return salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId)
                .stream()
                .map(this::mapToSalesDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateAiInsight(Long businessId, String prompt) {
        List<Inventory> inventoryList = inventoryRepository.findByBusinessId(businessId);
        long lowStockCount = inventoryList.stream().filter(i -> i.getStockLevel() < 5).count();

        return "AI Insight: You have " + lowStockCount + " products with low stock. Prompt: " + prompt;
    }

    private InventoryDto mapToInventoryDto(Inventory inventory) {
        return new InventoryDto(
                inventory.getProductId(),
                inventory.getProductName(),
                inventory.getStockLevel(),
                inventory.getPrice(),
                inventory.getBusiness().getBusiness_id()
        );
    }

    private SalesDto mapToSalesDto(Sales sale) {
        return new SalesDto(
                sale.getSaleId(),
                sale.getTotalAmount(),
                sale.getItemsCount(),
                sale.getSaleDate(),
                sale.getBusiness().getBusiness_id()
        );
    }
}