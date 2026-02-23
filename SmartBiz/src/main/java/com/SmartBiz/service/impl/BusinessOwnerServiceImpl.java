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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessOwnerServiceImpl implements BusinessOwnerService {

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final BusinessRepository businessRepository;

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
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Inventory inv = new Inventory();
        inv.setProductName(dto.getProductName());
        inv.setStockLevel(dto.getStockLevel());
        inv.setPrice(dto.getPrice());
        inv.setBusiness(business);

        Inventory saved = inventoryRepository.save(inv);
        return mapToInventoryDto(saved);
    }

    @Override
    public List<InventoryDto> getAllInventory(Long business_id) {
        return inventoryRepository.findByBusinessId(business_id)
                .stream().map(this::mapToInventoryDto).collect(Collectors.toList());
    }

    @Override
    public InventoryDto updateStock(Long productId, Integer quantity, Long business_id) {
        Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (!inv.getBusiness().getBusiness_id().equals(business_id))
            throw new RuntimeException("Unauthorized");

        inv.setStockLevel(quantity);
        return mapToInventoryDto(inventoryRepository.save(inv));
    }

    @Override
    public SalesDto recordSale(SalesDto dto) {
        Businesses business = businessRepository.findById(dto.getBusiness_id())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Sales sale = new Sales();
        sale.setTotalAmount(dto.getTotalAmount());
        sale.setItemsCount(dto.getItemsCount());
        sale.setSaleDate(dto.getSaleDate() != null ? dto.getSaleDate() : LocalDateTime.now());
        sale.setBusiness(business);

        return mapToSalesDto(salesRepository.save(sale));
    }

    @Override
    public List<SalesDto> getSalesHistory(Long business_id) {
        return salesRepository.findByBusinessIdOrderBySaleDateDesc(business_id)
                .stream().map(this::mapToSalesDto).collect(Collectors.toList());
    }

    @Override
    public String generateAiInsight(Long business_id, String prompt) {
        List<Inventory> inventory = inventoryRepository.findByBusinessId(business_id);
        long lowStockCount = inventory.stream().filter(i -> i.getStockLevel() < 5).count();

        return "AI Insight: You have " + lowStockCount + " products with low stock. Prompt: " + prompt;
    }

    // --- Helpers ---
    private InventoryDto mapToInventoryDto(Inventory inv) {
        return new InventoryDto(inv.getProductId(), inv.getProductName(), inv.getStockLevel(),
                inv.getPrice(), inv.getBusiness().getBusiness_id());
    }

    private SalesDto mapToSalesDto(Sales s) {
        return new SalesDto(s.getSaleId(), s.getTotalAmount(), s.getItemsCount(),
                s.getSaleDate(), s.getBusiness().getBusiness_id());
    }
}