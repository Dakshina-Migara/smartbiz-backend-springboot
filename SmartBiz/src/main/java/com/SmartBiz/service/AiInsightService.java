package com.SmartBiz.service;

import com.SmartBiz.dto.AiInsightRequestDto;
import com.SmartBiz.dto.AiInsightResponseDto;

import java.util.List;

public interface AiInsightService {

    AiInsightResponseDto generateInsight(AiInsightRequestDto request);

    List<AiInsightResponseDto> getHistory(Long businessId);

    List<AiInsightResponseDto> getHistoryByType(Long businessId, String type);

    List<String> getQuickQuestions(String type);
}
