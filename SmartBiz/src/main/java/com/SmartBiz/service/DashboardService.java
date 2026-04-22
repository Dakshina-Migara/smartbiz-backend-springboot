package com.SmartBiz.service;

import org.springframework.lang.NonNull;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getKPIs(@NonNull Long businessId);
}
