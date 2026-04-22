package com.SmartBiz.service;

import com.SmartBiz.dto.AiInsightRequestDto;
import com.SmartBiz.dto.AiInsightResponseDto;
import org.springframework.lang.NonNull;

import java.util.List;

public interface AiInsightService {

    AiInsightResponseDto generateInsight(@NonNull AiInsightRequestDto request);

    List<AiInsightResponseDto> getHistory(@NonNull Long businessId);

    List<AiInsightResponseDto> getHistoryByType(@NonNull Long businessId, @NonNull String type);

    List<String> getQuickQuestions(@NonNull String type);
}
