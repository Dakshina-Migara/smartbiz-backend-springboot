package com.SmartBiz.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getKPIs(Long businessId);
}
