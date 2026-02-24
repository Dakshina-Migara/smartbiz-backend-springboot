package com.SmartBiz.controller;

import com.SmartBiz.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AiController - REST API for AI-powered features.
 *
 * Endpoints:
 * POST /api/v1/business/{businessId}/ai/query → Natural language data query
 * POST /api/v1/business/{businessId}/ai/generate-email → Generate professional
 * email
 * POST /api/v1/business/{businessId}/ai/generate-post → Generate social media
 * post
 * POST /api/v1/business/{businessId}/ai/explain-invoice → Simplify invoice
 */
@RestController
@RequestMapping("/api/v1/business/{businessId}/ai")
public class AiController {

    private final AiService aiService;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, String>> queryData(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "");
        String result = aiService.queryData(businessId, prompt);
        return ResponseEntity.ok(Map.of("response", result));
    }

    @PostMapping("/generate-email")
    public ResponseEntity<Map<String, String>> generateEmail(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "");
        String result = aiService.generateEmail(businessId, prompt);
        return ResponseEntity.ok(Map.of("response", result));
    }

    @PostMapping("/generate-post")
    public ResponseEntity<Map<String, String>> generatePost(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "");
        String result = aiService.generatePost(businessId, prompt);
        return ResponseEntity.ok(Map.of("response", result));
    }

    @PostMapping("/explain-invoice")
    public ResponseEntity<Map<String, String>> explainInvoice(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        Long invoiceId = Long.parseLong(request.getOrDefault("invoiceId", "0"));
        String result = aiService.explainInvoice(businessId, invoiceId);
        return ResponseEntity.ok(Map.of("response", result));
    }
}
