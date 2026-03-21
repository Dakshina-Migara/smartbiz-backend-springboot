package com.SmartBiz.service.impl;

import com.SmartBiz.entity.Businesses;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final SalesRepository salesRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final AiRequestRepository aiRequestRepository;
    private final BusinessRepository businessRepository;

    @Override
    public Map<String, Object> getKPIs(Long businessId) {
        try {
            Map<String, Object> kpis = new LinkedHashMap<>();

            // 1. Optimized Revenue and Sales Count (One query instead of full list)
            Double totalRevenue = salesRepository.sumTotalAmountByBusinessId(businessId);
            Long salesCount = salesRepository.countByBusinessId(businessId);
            double revenueVal = totalRevenue != null ? totalRevenue : 0.0;

            kpis.put("totalRevenue", revenueVal);
            kpis.put("salesCount", salesCount != null ? salesCount : 0);

            // 2. Optimized Expenses (Already good)
            Double totalExpenses = paymentRepository.sumAmountByBusinessId(businessId);
            double expensesVal = totalExpenses != null ? totalExpenses : 0.0;
            kpis.put("totalExpenses", expensesVal);

            // 3. Profit Calculations
            double netProfit = revenueVal - expensesVal;
            double margin = revenueVal != 0 ? (netProfit / revenueVal) * 100 : 0;
            kpis.put("netProfit", netProfit);
            kpis.put("profitMargin", Math.round(margin * 10.0) / 10.0);

            // 4. Counts and Values (Optimized)
            kpis.put("lowStockAlerts", inventoryRepository.countLowStock(businessId));
            kpis.put("totalCustomers", customerRepository.countByBusinessId(businessId));
            kpis.put("inventoryValue", inventoryRepository.calculateInventoryValue(businessId));
            kpis.put("totalProducts", inventoryRepository.countByBusinessId(businessId));

            // 5. AI Token Usage (New)
            Long aiTokensUsed = aiRequestRepository.sumTokensByBusinessIdAndMonth(businessId);
            kpis.put("aiTokensUsedMonthly", aiTokensUsed != null ? aiTokensUsed : 0);

            // Fetch plan limit
            Businesses business = businessRepository.findById(businessId).orElse(null);
            if (business != null && business.getSubscription() != null) {
                kpis.put("aiTokenLimit", business.getSubscription().getAiTokenLimit());
                kpis.put("planName", business.getSubscription().getPlanName());
            } else {
                kpis.put("aiTokenLimit", 0);
                kpis.put("planName", "None");
            }

            return kpis;
        } catch (Exception e) {
            log.error("Error fetching dashboard KPIs for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to get dashboard statistics: " + e.getMessage());
        }
    }
}
