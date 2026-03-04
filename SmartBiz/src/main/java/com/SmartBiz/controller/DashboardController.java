package com.SmartBiz.controller;

import com.SmartBiz.repository.CustomerRepository;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.PaymentRepository;
import com.SmartBiz.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/dashboard")
public class DashboardController {

    private final SalesRepository salesRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public DashboardController(SalesRepository salesRepository, InventoryRepository inventoryRepository,
            CustomerRepository customerRepository, PaymentRepository paymentRepository) {
        this.salesRepository = salesRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKPIs(@PathVariable Long businessId) {
        Map<String, Object> kpis = new LinkedHashMap<>();

        var sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);
        double totalRevenue = sales.stream()
                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                .sum();
        kpis.put("totalRevenue", totalRevenue);
        kpis.put("salesCount", sales.size());

        Double totalExpenses = paymentRepository.sumAmountByBusinessId(businessId);
        kpis.put("totalExpenses", totalExpenses);

        double netProfit = totalRevenue - totalExpenses;
        double margin = totalRevenue != 0 ? (netProfit / totalRevenue) * 100 : 0;
        kpis.put("netProfit", netProfit);
        kpis.put("profitMargin", Math.round(margin * 10.0) / 10.0);

        Long lowStockAlerts = inventoryRepository.countLowStock(businessId);
        kpis.put("lowStockAlerts", lowStockAlerts);

        Long totalCustomers = customerRepository.countByBusinessId(businessId);
        kpis.put("totalCustomers", totalCustomers);

        Double inventoryValue = inventoryRepository.calculateInventoryValue(businessId);
        kpis.put("inventoryValue", inventoryValue);

        var inventory = inventoryRepository.findByBusinessId(businessId);
        kpis.put("totalProducts", inventory.size());

        return new ResponseEntity<>(kpis, HttpStatus.OK);
    }
}
