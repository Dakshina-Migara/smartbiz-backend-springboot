package com.SmartBiz.service.impl;

import com.SmartBiz.dto.AiInsightRequestDto;
import com.SmartBiz.dto.AiInsightResponseDto;
import com.SmartBiz.entity.AiRequest;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.repository.*;
import com.SmartBiz.service.AiInsightService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AiInsightServiceImpl implements AiInsightService {

    private static final Logger log = LoggerFactory.getLogger(AiInsightServiceImpl.class);

    private final AiRequestRepository aiRequestRepository;
    private final BusinessRepository businessRepository;
    private final SalesRepository salesRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    @Override
    public AiInsightResponseDto generateInsight(AiInsightRequestDto request) {
        try {
            Businesses business = businessRepository.findById(request.getBusinessId())
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + request.getBusinessId()));

            String response;
            switch (request.getType().toLowerCase()) {
                case "business_report":
                    response = generateBusinessReport(request.getBusinessId(), request.getPrompt());
                    break;
                case "email":
                    response = generateEmail(request.getBusinessId(), request.getPrompt());
                    break;
                case "marketing":
                    response = generateMarketingPost(request.getBusinessId(), request.getPrompt());
                    break;
                default:
                    response = "Unknown type. Please use: business_report, email, or marketing.";
            }

            int tokenEstimate = (request.getPrompt().length() + response.length()) / 4;

            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(request.getPrompt());
            aiRequest.setResponse(response);
            aiRequest.setType(request.getType().toLowerCase());
            aiRequest.setTokenUsed(tokenEstimate);
            aiRequest.setCreatedAt(LocalDateTime.now());
            aiRequest.setBusiness(business);

            AiRequest saved = aiRequestRepository.save(aiRequest);
            log.info("Generated AI insight type '{}' for business id: {}", request.getType(), request.getBusinessId());
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error generating AI insight: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate AI insight: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiInsightResponseDto> getHistory(Long businessId) {
        try {
            return aiRequestRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching AI history for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiInsightResponseDto> getHistoryByType(Long businessId, String type) {
        try {
            return aiRequestRepository.findByBusinessIdAndType(businessId, type.toLowerCase())
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching AI history by type for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getQuickQuestions(String type) {
        if (type == null)
            return Collections.emptyList();

        String cleanType = type.toLowerCase().replace("-", "_").trim();

        switch (cleanType) {
            case "business_report":
            case "report":
            case "business":
                return List.of("Top Products", "Profit Analysis", "Stock Status",
                        "How did I perform last month?", "What are my best selling items?");
            case "email":
            case "messaging":
                return List.of("Thank a customer for their purchase",
                        "Follow up on a pending payment",
                        "Announce a new product launch",
                        "Request feedback from a customer");
            case "marketing":
            case "social":
            case "post":
                return List.of("Announce a seasonal sale",
                        "Promote our top-selling product",
                        "Share a customer success story",
                        "Create a post about new arrivals");
            default:
                log.warn("Unknown quick question type requested: {}", type);
                return List.of("How can I improve my business?", "General business advice", "System help");
        }
    }

    // --- Data-driven response generators (placeholder for real AI) ---

    private String generateBusinessReport(Long businessId, String prompt) {
        Double totalRevenue = salesRepository.sumTotalAmountByBusinessId(businessId);
        Double totalExpenses = transactionRepository.sumExpensesByBusinessId(businessId);
        Long totalCustomers = customerRepository.countByBusinessId(businessId);
        Long lowStock = inventoryRepository.countLowStock(businessId);

        double revenue = totalRevenue != null ? totalRevenue : 0.0;
        double expenses = totalExpenses != null ? totalExpenses : 0.0;
        double profit = revenue - expenses;

        StringBuilder sb = new StringBuilder();
        sb.append("📊 Business Report\n\n");
        sb.append("Based on your question: \"").append(prompt).append("\"\n\n");
        sb.append("Here's your current business snapshot:\n\n");
        sb.append("💰 Total Revenue: $").append(String.format("%.2f", revenue)).append("\n");
        sb.append("📉 Total Expenses: $").append(String.format("%.2f", expenses)).append("\n");
        sb.append("📈 Net Profit: $").append(String.format("%.2f", profit)).append("\n");
        sb.append("👥 Total Customers: ").append(totalCustomers != null ? totalCustomers : 0).append("\n");
        sb.append("⚠️ Low Stock Alerts: ").append(lowStock != null ? lowStock : 0).append(" items\n\n");

        if (profit > 0) {
            sb.append("✅ Your business is profitable! Keep up the great work.\n");
        } else {
            sb.append("⚠️ Your expenses exceed revenue. Consider reviewing cost categories.\n");
        }

        return sb.toString();
    }

    private String generateEmail(Long businessId, String prompt) {
        StringBuilder sb = new StringBuilder();
        sb.append("📧 Generated Email\n\n");
        sb.append("Subject: ").append(prompt).append("\n\n");
        sb.append("Dear Valued Customer,\n\n");
        sb.append("Thank you for choosing our business. ");
        sb.append("We truly appreciate your continued support and trust in our services.\n\n");
        sb.append("Regarding \"").append(prompt).append("\", ");
        sb.append("we wanted to reach out and ensure you have the best experience with us.\n\n");
        sb.append("If you have any questions or need further assistance, please don't hesitate to contact us.\n\n");
        sb.append("Best regards,\n");
        sb.append("Your Business Team");
        return sb.toString();
    }

    private String generateMarketingPost(Long businessId, String prompt) {
        StringBuilder sb = new StringBuilder();
        sb.append("📱 Marketing Post\n\n");
        sb.append("🔥 ").append(prompt.toUpperCase()).append(" 🔥\n\n");
        sb.append("We're excited to share something special with you! ");
        sb.append("At our store, we believe in delivering quality products and exceptional service.\n\n");
        sb.append("🛒 Shop now and discover amazing deals!\n");
        sb.append("💫 Quality you can trust\n");
        sb.append("🚚 Fast & reliable delivery\n");
        sb.append("💬 24/7 Customer support\n\n");
        sb.append("#SmartBiz #BusinessGrowth #SpecialOffer #ShopNow");
        return sb.toString();
    }

    private AiInsightResponseDto mapToDto(AiRequest request) {
        AiInsightResponseDto dto = new AiInsightResponseDto();
        dto.setRequestId(request.getRequestId());
        dto.setPrompt(request.getPrompt());
        dto.setType(request.getType());
        dto.setResponse(request.getResponse());
        dto.setTokenUsed(request.getTokenUsed());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
