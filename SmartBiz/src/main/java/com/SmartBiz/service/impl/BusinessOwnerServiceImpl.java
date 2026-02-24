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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BusinessOwnerServiceImpl - Implementation of the BusinessOwnerService
 * interface.
 * Contains the actual business logic for business owner operations.
 */
@Service
@Transactional
public class BusinessOwnerServiceImpl implements BusinessOwnerService {

    private static final Logger log = LoggerFactory.getLogger(BusinessOwnerServiceImpl.class);

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
        try {
            Businesses business = businessRepository.findById(dto.getBusiness_id())
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusiness_id()));

            Inventory inventory = new Inventory();
            inventory.setProductName(dto.getProductName());
            inventory.setStockLevel(dto.getStockLevel());
            inventory.setPrice(dto.getPrice());
            inventory.setBusiness(business);

            Inventory saved = inventoryRepository.save(inventory);
            log.info("Added inventory product '{}' for business id: {}", dto.getProductName(), dto.getBusiness_id());
            return mapToInventoryDto(saved);
        } catch (Exception e) {
            log.error("Error adding inventory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add inventory: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory(Long businessId) {
        try {
            return inventoryRepository.findByBusinessId(businessId)
                    .stream()
                    .map(this::mapToInventoryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching inventory for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public InventoryDto updateStock(Long productId, Integer quantity, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + productId));

            if (!inventory.getBusiness().getBusiness_id().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            inventory.setStockLevel(quantity);
            Inventory updated = inventoryRepository.save(inventory);
            log.info("Updated stock for product id: {} to quantity: {}", productId, quantity);
            return mapToInventoryDto(updated);
        } catch (Exception e) {
            log.error("Error updating stock for product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update stock: " + e.getMessage());
        }
    }

    @Override
    public SalesDto recordSale(SalesDto dto) {
        try {
            Businesses business = businessRepository.findById(dto.getBusiness_id())
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusiness_id()));

            Sales sale = new Sales();
            sale.setTotalAmount(dto.getTotalAmount());
            sale.setItemsCount(dto.getItemsCount());
            sale.setSaleDate(dto.getSaleDate() != null ? dto.getSaleDate() : LocalDateTime.now());
            sale.setBusiness(business);

            Sales savedSale = salesRepository.save(sale);
            log.info("Recorded sale for business id: {}, amount: {}", dto.getBusiness_id(), dto.getTotalAmount());
            return mapToSalesDto(savedSale);
        } catch (Exception e) {
            log.error("Error recording sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record sale: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesDto> getSalesHistory(Long businessId) {
        try {
            return salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId)
                    .stream()
                    .map(this::mapToSalesDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching sales history for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteProduct(Long productId, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            if (!inventory.getBusiness().getBusiness_id().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            inventoryRepository.delete(inventory);
            log.info("Deleted product id: {} from business id: {}", productId, businessId);
        } catch (Exception e) {
            log.error("Error deleting product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete product: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateAiInsight(Long businessId, String prompt) {
        try {
            List<Inventory> inventoryList = inventoryRepository.findByBusinessId(businessId);
            long lowStockCount = inventoryList.stream().filter(i -> i.getStockLevel() < 5).count();
            return "AI Insight: You have " + lowStockCount + " products with low stock. Prompt: " + prompt;
        } catch (Exception e) {
            log.error("Error generating AI insight for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate AI insight: " + e.getMessage());
        }
    }

    // ==================== PRIVATE HELPER METHODS (Entity ↔ DTO Mapping)
    // ====================

    private InventoryDto mapToInventoryDto(Inventory inventory) {
        return new InventoryDto(
                inventory.getProductId(),
                inventory.getProductName(),
                inventory.getStockLevel(),
                inventory.getPrice(),
                inventory.getBusiness().getBusiness_id());
    }

    private SalesDto mapToSalesDto(Sales sale) {
        return new SalesDto(
                sale.getSaleId(),
                sale.getTotalAmount(),
                sale.getItemsCount(),
                sale.getSaleDate(),
                sale.getBusiness().getBusiness_id());
    }
}