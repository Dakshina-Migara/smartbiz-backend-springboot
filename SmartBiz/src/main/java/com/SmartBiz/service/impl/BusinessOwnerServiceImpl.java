package com.SmartBiz.service.impl;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;
import com.SmartBiz.entity.*;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.BusinessOwnerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BusinessOwnerServiceImpl implements BusinessOwnerService {

    private static final Logger log = LoggerFactory.getLogger(BusinessOwnerServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final BusinessRepository businessRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final SaleItemRepository saleItemRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final InvoiceRepository invoiceRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public InventoryDto addInventory(@NonNull InventoryDto dto) {
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

            // Log activity
            saveActivityLog(business, "Inventory", "Added Product: " + dto.getProductName(), 0);

            return mapToInventoryDto(saved);
        } catch (Exception e) {
            log.error("Error adding inventory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add inventory: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory(@NonNull Long businessId) {
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
    public List<InventoryDto> searchInventory(@NonNull Long businessId, @NonNull String query) {
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
    public List<InventoryDto> filterInventoryByStatus(@NonNull Long businessId, @NonNull String status) {
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
    public InventoryDto updateStock(@NonNull Long productId, @NonNull Integer quantity, @NonNull Long businessId) {
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

            // Log activity
            saveActivityLog(inventory.getBusiness(), "Inventory",
                    "Updated Stock: " + inventory.getProductName() + " to " + quantity, 0);

            return mapToInventoryDto(updated);
        } catch (Exception e) {
            log.error("Error updating stock for product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update stock: " + e.getMessage());
        }
    }

    @Override
    public InventoryDto adjustStock(@NonNull Long productId, int adjustment, @NonNull Long businessId) {
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
    public InventoryDto updateProduct(@NonNull Long productId, @NonNull InventoryDto dto, @NonNull Long businessId) {
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
    public Map<String, Object> getInventoryStats(@NonNull Long businessId) {
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
    public SalesDto recordSale(@NonNull SalesDto dto) {
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

            // Log activity
            saveActivityLog(business, "Sales", "Recorded Sale: $" + dto.getTotalAmount(), 0);

            return mapToSalesDto(savedSale);
        } catch (Exception e) {
            log.error("Error recording sale: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to record sale: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesDto> getSalesHistory(@NonNull Long businessId) {
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
    public List<SalesDto> searchSales(@NonNull Long businessId, @NonNull String query) {
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
    public void deleteProduct(@NonNull Long productId, @NonNull Long businessId) {
        try {
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            if (!inventory.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            // Check if there are any sale items referencing this product
            if (saleItemRepository.countByProductId(productId) > 0) {
                throw new RuntimeException("Cannot delete product because it has associated sales records. Please update its stock to 0 instead.");
            }

            inventoryRepository.delete(inventory);
            log.info("Deleted product id: {} from business id: {}", productId, businessId);
        } catch (Exception e) {
            log.error("Error deleting product id {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete product: " + e.getMessage());
        }
    }

    @Override
    public SalesDto recordMobileSale(@NonNull Long businessId, @NonNull com.SmartBiz.dto.MobileSaleRequestDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            // Fault 3 Fix: Null check on customerName
            if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
                throw new RuntimeException("Customer name is required for mobile sale");
            }

            // Fault 1 Fix: Use optimized DB query instead of loading all customers
            Customer customer = customerRepository.findByBusinessIdAndNameIgnoreCase(businessId, dto.getCustomerName())
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setName(dto.getCustomerName());
                        newCustomer.setEmail(dto.getCustomerEmail());
                        newCustomer.setPhone(dto.getCustomerPhone());
                        newCustomer.setBusiness(business);
                        newCustomer.setTotalPurchases(0.0);
                        return customerRepository.save(newCustomer);
                    });

            // Fault 2 Fix: VALIDATE all stock first before any writes
            List<Inventory> inventoriesToUpdate = new ArrayList<>();
            double totalAmount = 0;
            int totalQty = 0;
            List<SaleItem> saleItems = new ArrayList<>();

            for (com.SmartBiz.dto.SaleItemDto itemDto : dto.getItems()) {
                Inventory inventory = inventoryRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

                if (inventory.getStockLevel() < itemDto.getQty()) {
                    throw new RuntimeException("Insufficient stock for product: " + inventory.getProductName()
                            + " (available: " + inventory.getStockLevel() + ", requested: " + itemDto.getQty() + ")");
                }

                SaleItem saleItem = new SaleItem();
                saleItem.setProduct(inventory);
                saleItem.setQty(itemDto.getQty());
                saleItem.setPrice(itemDto.getPrice());

                totalAmount += (itemDto.getPrice() * itemDto.getQty());
                totalQty += itemDto.getQty();
                saleItems.add(saleItem);

                // Prepare stock deduction but don't save yet
                inventory.setStockLevel(inventory.getStockLevel() - itemDto.getQty());
                inventoriesToUpdate.add(inventory);
            }

            // Now that ALL validations passed, save everything together
            // 1. Save Sale first
            Sales sale = new Sales();
            sale.setBusiness(business);
            sale.setCustomer(customer);
            sale.setTotalAmount(totalAmount);
            sale.setItemsCount(totalQty);
            sale.setPaymentMethod(dto.getPaymentMethod());
            sale.setStatus(dto.getStatus());
            sale.setSaleDate(LocalDateTime.now());

            Sales savedSale = salesRepository.save(sale);

            // 2. Link and save sale items
            for (SaleItem si : saleItems) {
                si.setSale(savedSale);
                saleItemRepository.save(si);
            }

            // 3. Deduct stock (after sale is confirmed)
            for (Inventory inv : inventoriesToUpdate) {
                inventoryRepository.save(inv);
            }

            // 4. Update customer total purchases
            customer.setTotalPurchases(customer.getTotalPurchases() + totalAmount);
            customerRepository.save(customer);

            log.info("Mobile sale recorded: {} items, total: {}", totalQty, totalAmount);

            // 5. Create an Invoice record for this sale so it shows up in the Invoices page
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(savedSale.getInvoiceNumber());
            invoice.setCustomerName(customer.getName());
            invoice.setCustomerEmail(customer.getEmail());
            invoice.setSale(savedSale);
            invoice.setBusiness(business);
            invoice.setIssuedDate(LocalDateTime.now());
            invoice.setCreatedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            log.info("Invoice created for sale: {}", savedSale.getInvoiceNumber());

            // Log activity
            saveActivityLog(business, "Sales", "Mobile Sale Recorded: $" + totalAmount, 0);

            // Return FULL sale details including items for the invoice view
            return getSaleById(businessId, savedSale.getSaleId());
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

    @Override
    @Transactional(readOnly = true)
    public SalesDto getSaleById(@NonNull Long businessId, @NonNull Long saleId) {
        try {
            Sales sale = salesRepository.findById(saleId)
                    .orElseThrow(() -> new RuntimeException("Sale not found with id: " + saleId));

            if (!sale.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch");
            }

            SalesDto dto = mapToSalesDto(sale);
            // Also map items
            if (sale.getSaleItems() != null) {
                List<com.SmartBiz.dto.SaleItemDto> itemDtos = sale.getSaleItems().stream().map(si -> {
                    com.SmartBiz.dto.SaleItemDto itemDto = new com.SmartBiz.dto.SaleItemDto();
                    itemDto.setSaleItemId(si.getSaleItemId());
                    itemDto.setProductId(si.getProduct().getProductId());
                    itemDto.setProductName(si.getProduct().getProductName());
                    itemDto.setQty(si.getQty());
                    itemDto.setPrice(si.getPrice());
                    itemDto.setSaleId(saleId);
                    return itemDto;
                }).collect(Collectors.toList());
                dto.setItems(itemDtos);
            }
            return dto;
        } catch (Exception e) {
            log.error("Error fetching sale id {}: {}", saleId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch sale details: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteSale(@NonNull Long businessId, @NonNull Long saleId) {
        try {
            Sales sale = salesRepository.findById(saleId)
                    .orElseThrow(() -> new RuntimeException("Sale not found with id: " + saleId));

            if (!sale.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Business mismatch for delete operation");
            }

            // 1. Group stock restoration by product (Performance optimization & safety)
            if (sale.getSaleItems() != null) {
                Map<Long, Integer> productRestorations = new HashMap<>();
                for (SaleItem item : sale.getSaleItems()) {
                    if (item.getProduct() != null && item.getQty() != null) {
                        Long pid = item.getProduct().getProductId();
                        productRestorations.put(pid, productRestorations.getOrDefault(pid, 0) + item.getQty());
                    }
                }

                // Apply grouped stock updates
                for (Map.Entry<Long, Integer> entry : productRestorations.entrySet()) {
                    inventoryRepository.findById(entry.getKey()).ifPresent(product -> {
                        int currentStock = product.getStockLevel() != null ? product.getStockLevel() : 0;
                        product.setStockLevel(currentStock + entry.getValue());
                        inventoryRepository.save(product);
                    });
                }
            }

            // 2. Update customer total purchases (deduct this sale)
            if (sale.getCustomer() != null && sale.getTotalAmount() != null) {
                Customer customer = sale.getCustomer();
                double currentTotal = customer.getTotalPurchases() != null ? customer.getTotalPurchases() : 0.0;
                customer.setTotalPurchases(Math.max(0, currentTotal - sale.getTotalAmount()));
                customerRepository.save(customer);
            }

            // 3. Delete the sale record (CASCADE will handle SaleItems, Payments, and
            // Invoices)
            salesRepository.delete(sale);
            log.info("Successfully deleted sale id: {} for business: {}. Stock restored and customer total updated.",
                    saleId, businessId);
        } catch (Exception e) {
            log.error("Failed to delete sale id {}: {}", saleId, e.getMessage(), e);
            throw new RuntimeException("Error during sale deletion: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void subscribeToPlan(@NonNull Long businessId, @NonNull Long planId) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + planId));

            business.setSubscription(plan);
            businessRepository.save(business);
            log.info("Business id: {} subscribed to plan: {}", businessId, plan.getPlanName());
        } catch (Exception e) {
            log.error("Error subscribing business id {} to plan id {}: {}", businessId, planId, e.getMessage(), e);
            throw new RuntimeException("Failed to subscribe to plan: " + e.getMessage());
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

    private void saveActivityLog(Businesses business, String feature, String action, Integer aiTokens) {
        try {
            ActivityLog activityLog = new ActivityLog();
            activityLog.setBusiness(business);
            activityLog.setFeature(feature);
            activityLog.setAction(action);
            activityLog.setAiTokens(aiTokens != null ? aiTokens : 0);
            activityLog.setTimestamp(LocalDateTime.now());
            activityLogRepository.save(activityLog);
        } catch (Exception e) {
            log.error("Failed to save activity log: {}", e.getMessage());
        }
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
            dto.setCustomerEmail(sale.getCustomer().getEmail());
            dto.setCustomerPhone(sale.getCustomer().getPhone());
        }

        return dto;
    }
}