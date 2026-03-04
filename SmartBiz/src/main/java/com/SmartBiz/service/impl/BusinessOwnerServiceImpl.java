package com.SmartBiz.service.impl;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.entity.*;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.BusinessOwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusinessOwnerServiceImpl implements BusinessOwnerService {

    private static final Logger log = LoggerFactory.getLogger(BusinessOwnerServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final BusinessRepository businessRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final SaleItemRepository saleItemRepository;

    @Autowired
    public BusinessOwnerServiceImpl(InventoryRepository inventoryRepository,
            SalesRepository salesRepository,
            BusinessRepository businessRepository,
            SupplierRepository supplierRepository,
            CustomerRepository customerRepository,
            SaleItemRepository saleItemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.salesRepository = salesRepository;
        this.businessRepository = businessRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.saleItemRepository = saleItemRepository;
    }

    @Override
    public InventoryDto addInventory(InventoryDto dto) {
        try {
            Businesses business = businessRepository.findById(dto.getBusinessId())
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusinessId()));

            Inventory inventory = new Inventory();
            inventory.setProductName(dto.getProductName());
            inventory.setSku(dto.getSku());
            inventory.setCategory(dto.getCategory());
            inventory.setPrice(dto.getPrice());
            inventory.setCost(dto.getCost());
            inventory.setStockLevel(dto.getStockLevel());
            inventory.setMinStockLevel(dto.getMinStockLevel());
            inventory.setBusiness(business);

            if (dto.getSupplierId() != null) {
                Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + dto.getSupplierId()));
                inventory.setSupplier(supplier);
            }

            Inventory saved = inventoryRepository.save(inventory);
            log.info("Added inventory product '{}' for business id: {}", dto.getProductName(), dto.getBusinessId());
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
            throw new RuntimeException("Failed to fetch inventory: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> searchInventory(Long businessId, String query) {
        try {
            return inventoryRepository.searchByBusinessId(businessId, query)
                    .stream()
                    .map(this::mapToInventoryDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching inventory for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to search inventory: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> filterInventoryByStatus(Long businessId, String status) {
        try {
            List<Inventory> items;
            switch (status.toLowerCase()) {
                case "low_stock":
                    items = inventoryRepository.findLowStockByBusinessId(businessId);
                    break;
                case "out_of_stock":
                    items = inventoryRepository.findOutOfStockByBusinessId(businessId);
                    break;
                case "in_stock":
                    items = inventoryRepository.findInStockByBusinessId(businessId);
                    break;
                default:
                    items = inventoryRepository.findByBusinessId(businessId);
            }
            return items.stream().map(this::mapToInventoryDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error filtering inventory for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to filter inventory: " + e.getMessage());
        }
    }

    @Override
    public InventoryDto updateStock(Long productId, Integer quantity, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + productId));

            if (!inventory.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException(
                        "Unauthorized: Business mismatch for product " + productId + " and business " + businessId);
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
    public InventoryDto adjustStock(Long productId, int adjustment, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            if (!inventory.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            int newStock = inventory.getStockLevel() + adjustment;
            if (newStock < 0) {
                throw new RuntimeException("Stock cannot go below 0");
            }

            inventory.setStockLevel(newStock);
            Inventory updated = inventoryRepository.save(inventory);
            log.info("Adjusted stock for product id: {} by {}, new level: {}", productId, adjustment, newStock);
            return mapToInventoryDto(updated);
        } catch (Exception e) {
            log.error("Error adjusting stock for product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to adjust stock: " + e.getMessage());
        }
    }

    @Override
    public InventoryDto updateProduct(Long productId, InventoryDto dto, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            if (!inventory.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            inventory.setProductName(dto.getProductName());
            inventory.setSku(dto.getSku());
            inventory.setCategory(dto.getCategory());
            inventory.setPrice(dto.getPrice());
            inventory.setCost(dto.getCost());
            inventory.setStockLevel(dto.getStockLevel());
            inventory.setMinStockLevel(dto.getMinStockLevel());

            if (dto.getSupplierId() != null) {
                Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + dto.getSupplierId()));
                inventory.setSupplier(supplier);
            } else {
                inventory.setSupplier(null);
            }

            Inventory updated = inventoryRepository.save(inventory);
            log.info("Updated product id: {} for business id: {}", productId, businessId);
            return mapToInventoryDto(updated);
        } catch (Exception e) {
            log.error("Error updating product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryStats(Long businessId) {
        try {
            Map<String, Object> stats = new LinkedHashMap<>();
            var items = inventoryRepository.findByBusinessId(businessId);
            stats.put("totalItems", items.size());
            stats.put("lowStockAlerts", inventoryRepository.countLowStock(businessId));
            stats.put("outOfStock", inventoryRepository.countOutOfStock(businessId));
            stats.put("totalValue", inventoryRepository.calculateInventoryValue(businessId));
            return stats;
        } catch (Exception e) {
            log.error("Error fetching inventory stats for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get inventory stats: " + e.getMessage());
        }
    }

    @Override
    public SalesDto recordSale(SalesDto dto) {
        try {
            Businesses business = businessRepository.findById(dto.getBusinessId())
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + dto.getBusinessId()));

            Sales sale = new Sales();
            sale.setTotalAmount(dto.getTotalAmount());
            sale.setItemsCount(dto.getItemsCount());
            sale.setPaymentMethod(dto.getPaymentMethod());
            sale.setStatus(dto.getStatus() != null ? dto.getStatus() : "completed");
            sale.setSaleDate(dto.getSaleDate() != null ? dto.getSaleDate() : LocalDateTime.now());
            sale.setBusiness(business);

            if (dto.getCustomerId() != null) {
                Customer customer = customerRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + dto.getCustomerId()));
                sale.setCustomer(customer);

                // Update customer totalPurchases
                double currentTotal = customer.getTotalPurchases() != null ? customer.getTotalPurchases() : 0.0;
                customer.setTotalPurchases(currentTotal + dto.getTotalAmount());
                customerRepository.save(customer);
            }

            Sales savedSale = salesRepository.save(sale);
            log.info("Recorded sale for business id: {}, amount: {}", dto.getBusinessId(), dto.getTotalAmount());
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
            throw new RuntimeException("Failed to fetch sales history: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesDto> searchSales(Long businessId, String query) {
        try {
            return salesRepository.searchByBusinessId(businessId, query)
                    .stream()
                    .map(this::mapToSalesDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching sales for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to search sales: " + e.getMessage());
        }
    }

    @Override
    public void deleteProduct(Long productId, Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            if (!inventory.getBusiness().getBusinessId().equals(businessId)) {
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
    public SalesDto recordMobileSale(Long businessId, com.SmartBiz.dto.MobileSaleRequestDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            // 1. Find or create customer
            Customer customer = customerRepository.findByBusinessId(businessId).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(dto.getCustomerName()))
                    .findFirst()
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setName(dto.getCustomerName());
                        newCustomer.setEmail(dto.getCustomerEmail());
                        newCustomer.setPhone(dto.getCustomerPhone());
                        newCustomer.setBusiness(business);
                        newCustomer.setTotalPurchases(0.0);
                        return customerRepository.save(newCustomer);
                    });

            // 2. Process sale items and calculate total
            double totalAmount = 0;
            int totalQty = 0;
            List<SaleItem> saleItems = new ArrayList<>();

            for (com.SmartBiz.dto.SaleItemDto itemDto : dto.getItems()) {
                Inventory inventory = inventoryRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

                if (inventory.getStockLevel() < itemDto.getQty()) {
                    throw new RuntimeException("Insufficient stock for product: " + inventory.getProductName());
                }

                // Deduct stock
                inventory.setStockLevel(inventory.getStockLevel() - itemDto.getQty());
                inventoryRepository.save(inventory);

                SaleItem saleItem = new SaleItem();
                saleItem.setProduct(inventory);
                saleItem.setQty(itemDto.getQty());
                saleItem.setPrice(itemDto.getPrice());

                totalAmount += (itemDto.getPrice() * itemDto.getQty());
                totalQty += itemDto.getQty();
                saleItems.add(saleItem);
            }

            // 3. Save Sale
            Sales sale = new Sales();
            sale.setBusiness(business);
            sale.setCustomer(customer);
            sale.setTotalAmount(totalAmount);
            sale.setItemsCount(totalQty);
            sale.setPaymentMethod(dto.getPaymentMethod());
            sale.setStatus(dto.getStatus());
            sale.setSaleDate(LocalDateTime.now());

            Sales savedSale = salesRepository.save(sale);

            // 4. Link items to sale and save
            for (SaleItem si : saleItems) {
                si.setSale(savedSale);
                saleItemRepository.save(si);
            }

            // 5. Update customer total purchases
            customer.setTotalPurchases(customer.getTotalPurchases() + totalAmount);
            customerRepository.save(customer);

            log.info("Mobile sale recorded: {} items, total: {}", totalQty, totalAmount);
            return mapToSalesDto(savedSale);
        } catch (Exception e) {
            log.error("Error recording mobile sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record mobile sale: " + e.getMessage());
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

    private InventoryDto mapToInventoryDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setProductId(inventory.getProductId());
        dto.setProductName(inventory.getProductName());
        dto.setSku(inventory.getSku());
        dto.setCategory(inventory.getCategory());
        dto.setPrice(inventory.getPrice());
        dto.setCost(inventory.getCost());
        dto.setStockLevel(inventory.getStockLevel());
        dto.setMinStockLevel(inventory.getMinStockLevel());
        dto.setBusinessId(inventory.getBusiness().getBusinessId());

        if (inventory.getSupplier() != null) {
            dto.setSupplierId(inventory.getSupplier().getSupplierId());
            dto.setSupplierName(inventory.getSupplier().getName());
        }

        if (inventory.getPrice() != null && inventory.getStockLevel() != null) {
            dto.setStockValue(inventory.getPrice() * inventory.getStockLevel());
        }

        if (inventory.getStockLevel() == 0) {
            dto.setStockStatus("Out of Stock");
        } else if (inventory.getMinStockLevel() != null && inventory.getStockLevel() <= inventory.getMinStockLevel()) {
            dto.setStockStatus("Low Stock");
        } else {
            dto.setStockStatus("In Stock");
        }

        return dto;
    }

    private SalesDto mapToSalesDto(Sales sale) {
        SalesDto dto = new SalesDto();
        dto.setSaleId(sale.getSaleId());
        dto.setInvoiceNumber(sale.getInvoiceNumber());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setItemsCount(sale.getItemsCount());
        dto.setPaymentMethod(sale.getPaymentMethod());
        dto.setStatus(sale.getStatus());
        dto.setSaleDate(sale.getSaleDate());
        dto.setBusinessId(sale.getBusiness().getBusinessId());

        if (sale.getCustomer() != null) {
            dto.setCustomerId(sale.getCustomer().getCustomerId());
            dto.setCustomerName(sale.getCustomer().getName());
        }

        return dto;
    }
}