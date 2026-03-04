package com.SmartBiz.controller;

import com.SmartBiz.dto.AiInsightRequestDto;
import com.SmartBiz.dto.AiInsightResponseDto;
import com.SmartBiz.service.AiInsightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        return new ResponseEntity<>(aiInsightService.generateInsight(request), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AiInsightResponseDto>> getHistory(@PathVariable Long businessId,
            @RequestParam(required = false) String type) {
        List<AiInsightResponseDto> history = (type != null) ? aiInsightService.getHistoryByType(businessId, type)
                : aiInsightService.getHistory(businessId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @GetMapping("/quick-questions")
    public ResponseEntity<List<String>> getQuickQuestions(
            @PathVariable Long businessId,
            @RequestParam String type) {
        List<String> questions = aiInsightService.getQuickQuestions(type);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}
