package com.SmartBiz.controller;

import com.SmartBiz.dto.AiInsightRequestDto;
import com.SmartBiz.dto.AiInsightResponseDto;
import com.SmartBiz.service.AiInsightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/{businessId}/ai-insights")
public class AiInsightController {

    private final AiInsightService aiInsightService;

    @Autowired
    public AiInsightController(AiInsightService aiInsightService) {
        this.aiInsightService = aiInsightService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AiInsightResponseDto> generateInsight(@PathVariable Long businessId,
            @Valid @RequestBody AiInsightRequestDto request) {
        request.setBusinessId(businessId);
        return ResponseEntity.ok(aiInsightService.generateInsight(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AiInsightResponseDto>> getHistory(@PathVariable Long businessId,
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(aiInsightService.getHistoryByType(businessId, type));
        }
        return ResponseEntity.ok(aiInsightService.getHistory(businessId));
    }

    @GetMapping("/quick-questions")
    public ResponseEntity<List<String>> getQuickQuestions(@RequestParam String type) {
        return ResponseEntity.ok(aiInsightService.getQuickQuestions(type));
    }
}
