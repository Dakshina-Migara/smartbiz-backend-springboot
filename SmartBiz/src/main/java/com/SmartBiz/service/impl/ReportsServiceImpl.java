package com.SmartBiz.service.impl;

import com.SmartBiz.entity.Inventory;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.SaleItemRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.repository.TransactionRepository;
import com.SmartBiz.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private static final Logger log = LoggerFactory.getLogger(ReportsServiceImpl.class);

    private final SalesRepository salesRepository;
    private final TransactionRepository transactionRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<Map<String, Object>> getSalesTrend(Long businessId) {
        try {
            List<Object[]> rows = salesRepository.dailySalesLast30Days(businessId);
            return rows.stream().map(row -> {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", row[0].toString());
                point.put("amount", row[1]);
                return point;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching sales trend for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get sales trend: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMonthlyOverview(Long businessId) {
        try {
            Map<String, Object> overview = new LinkedHashMap<>();
            Double revenue = salesRepository.sumCurrentMonthRevenue(businessId);
            Double expenses = transactionRepository.sumCurrentMonthExpenses(businessId);
            double revenueVal = revenue != null ? revenue : 0.0;
            double expensesVal = expenses != null ? expenses : 0.0;

            overview.put("revenue", revenueVal);
            overview.put("expenses", expensesVal);
            overview.put("profit", revenueVal - expensesVal);
            return overview;
        } catch (Exception e) {
            log.error("Error fetching monthly overview for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get monthly overview: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(Long businessId) {
        try {
            List<Object[]> rows = saleItemRepository.findTopSellingProducts(businessId);
            return rows.stream().map(row -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productName", row[0]);
                item.put("totalQty", row[1]);
                return item;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching top selling products for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get top selling products: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getExpensesByCategory(Long businessId) {
        try {
            List<Object[]> rows = transactionRepository.expensesByCategory(businessId);
            return rows.stream().map(row -> {
                Map<String, Object> cat = new LinkedHashMap<>();
                cat.put("category", row[0]);
                cat.put("amount", row[1]);
                return cat;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching expenses by category for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get expenses by category: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getLowStockAlerts(Long businessId) {
        try {
            List<Inventory> lowStock = inventoryRepository.findLowStockByBusinessId(businessId);
            List<Inventory> outOfStock = inventoryRepository.findOutOfStockByBusinessId(businessId);

            List<Inventory> combined = new ArrayList<>(lowStock);
            combined.addAll(outOfStock);

            return combined.stream().map(item -> {
                Map<String, Object> alert = new LinkedHashMap<>();
                alert.put("productName", item.getProductName());
                alert.put("sku", item.getSku());
                alert.put("stockLevel", item.getStockLevel());
                alert.put("minStockLevel", item.getMinStockLevel());
                return alert;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching low stock alerts for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get low stock alerts: " + e.getMessage());
        }
    }
}
