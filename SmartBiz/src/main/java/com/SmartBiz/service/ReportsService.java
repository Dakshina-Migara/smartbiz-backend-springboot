package com.SmartBiz.service;

import java.util.List;
import java.util.Map;

public interface ReportsService {

    List<Map<String, Object>> getSalesTrend(Long businessId);

    Map<String, Object> getMonthlyOverview(Long businessId);

    List<Map<String, Object>> getTopSellingProducts(Long businessId);

    List<Map<String, Object>> getExpensesByCategory(Long businessId);

    List<Map<String, Object>> getLowStockAlerts(Long businessId);
}
