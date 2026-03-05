package com.SmartBiz.controller;

import com.SmartBiz.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/{businessId}/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, String>> queryData(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String result = aiService.queryData(businessId, request.getOrDefault("prompt", ""));
        return new ResponseEntity<>(Map.of("response", result), HttpStatus.OK);
    }

    @PostMapping("/generate-email")
    public ResponseEntity<Map<String, String>> generateEmail(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String result = aiService.generateEmail(businessId, request.getOrDefault("prompt", ""));
        return new ResponseEntity<>(Map.of("response", result), HttpStatus.OK);
    }

    @PostMapping("/generate-post")
    public ResponseEntity<Map<String, String>> generatePost(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        String result = aiService.generatePost(businessId, request.getOrDefault("prompt", ""));
        return new ResponseEntity<>(Map.of("response", result), HttpStatus.OK);
    }

    @PostMapping("/explain-invoice")

    public ResponseEntity<Map<String, String>> explainInvoice(@PathVariable Long businessId,
            @RequestBody Map<String, String> request) {
        Long invoiceId = Long.parseLong(request.getOrDefault("invoiceId", "0"));
        String result = aiService.explainInvoice(businessId, invoiceId);
        return new ResponseEntity<>(Map.of("response", result), HttpStatus.OK);
    }
}