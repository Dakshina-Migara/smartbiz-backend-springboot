package com.SmartBiz.service.impl;

import com.SmartBiz.entity.AiRequest;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Inventory;
import com.SmartBiz.entity.Invoice;
import com.SmartBiz.entity.Sales;
import com.SmartBiz.entity.SubscriptionPlan;
import com.SmartBiz.repository.AiRequestRepository;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.InvoiceRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.AiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

        private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
        private final InventoryRepository inventoryRepository;
        private final SalesRepository salesRepository;
        private final InvoiceRepository invoiceRepository;
        private final AiRequestRepository aiRequestRepository;
        private final BusinessRepository businessRepository;
        private final ChatModel chatModel;

        private void validateTokenLimit(Long businessId) {
                Businesses business = businessRepository.findById(businessId)
                                .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

                SubscriptionPlan plan = business.getSubscription();
                if (plan == null) {
                        throw new RuntimeException("No active subscription found for this business.");
                }

                Long usedTokens = aiRequestRepository.sumTokensByBusinessIdAndMonth(businessId);
                if (usedTokens == null)
                        usedTokens = 0L;

                if (usedTokens >= plan.getAiTokenLimit()) {
                        log.warn("Business {} has exceeded their AI token limit of {}", businessId,
                                        plan.getAiTokenLimit());
                        throw new RuntimeException("AI Token limit reached. Please upgrade your subscription plan.");
                }
        }

        private void saveAiRequest(Long businessId, String prompt, String response, String type) {
                try {
                        Businesses business = businessRepository.findById(businessId).orElse(null);
                        if (business == null)
                                return;

                        int tokenEstimate = (prompt.length() + response.length()) / 4;

                        AiRequest aiRequest = new AiRequest();
                        aiRequest.setPrompt(prompt);
                        aiRequest.setResponse(response);
                        aiRequest.setType(type);
                        aiRequest.setTokenUsed(tokenEstimate);
                        aiRequest.setCreatedAt(LocalDateTime.now());
                        aiRequest.setBusiness(business);

                        aiRequestRepository.save(aiRequest);
                } catch (Exception e) {
                        log.error("Error saving AI request usage", e);
                }
        }

        @Override
        @Transactional
        public String queryData(Long businessId, String prompt) {
                List<Inventory> inventory = inventoryRepository.findByBusinessId(businessId);
                List<Sales> sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);

                long lowStockCount = inventory.stream().filter(i -> i.getStockLevel() < 5).count();
                double totalRevenue = sales.stream()
                                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                                .sum();

                String topItems = inventory.stream()
                                .limit(5)
                                .map(i -> i.getProductName() + " (Stock: " + i.getStockLevel() + ")")
                                .collect(Collectors.joining(", "));

                String systemContext = String.format(
                                "You are SmartBiz AI, an assistant for small business owners. " +
                                                "Current Business Snapshot: %d products in inventory, %d with low stock (below 5 units). "
                                                +
                                                "Total revenue from %d transactions: $%.2f. " +
                                                "Top Inventory Items: %s. " +
                                                "Provide a professional and helpful insight based on the user's query.",
                                inventory.size(), lowStockCount, sales.size(), totalRevenue, topItems);

                validateTokenLimit(businessId);
                log.info("AI query for business {}: {}", businessId, prompt);
                try {
                        String fullPrompt = systemContext + "\nUser Query: " + prompt;
                        String result = chatModel.call(fullPrompt);
                        saveAiRequest(businessId, prompt, result, "query");
                        return result;
                } catch (Exception e) {
                        log.error("Error calling AI service", e);
                        if (e.getMessage().contains("Token limit reached")) {
                                throw e;
                        }
                        return "I'm sorry, I'm having trouble connecting to the AI service right now. " +
                                        "Summary: You have " + inventory.size() + " products and revenue of $"
                                        + totalRevenue;
                }
        }

        @Override
        @Transactional
        public String generateEmail(Long businessId, String prompt) {
                validateTokenLimit(businessId);
                log.info("AI email generation for business {}: {}", businessId, prompt);
                try {
                        String systemPrompt = "You are an expert business communicator. Generate a professional email based on the following request: ";
                        String result = chatModel.call(systemPrompt + prompt);
                        saveAiRequest(businessId, prompt, result, "email");
                        return result;
                } catch (Exception e) {
                        log.error("Error generating AI email", e);
                        if (e.getMessage().contains("Token limit reached")) {
                                throw e;
                        }
                        return "Failed to generate email via AI. Original request: " + prompt;
                }
        }

        @Override
        @Transactional
        public String generatePost(Long businessId, String prompt) {
                validateTokenLimit(businessId);
                log.info("AI social media post for business {}: {}", businessId, prompt);
                try {
                        String systemPrompt = "You are a social media expert. Generate an engaging post with relevant emojis and hashtags based on: ";
                        String result = chatModel.call(systemPrompt + prompt);
                        saveAiRequest(businessId, prompt, result, "marketing");
                        return result;
                } catch (Exception e) {
                        log.error("Error generating AI post", e);
                        if (e.getMessage().contains("Token limit reached")) {
                                throw e;
                        }
                        return "Failed to generate social media post via AI. Original request: " + prompt;
                }
        }

        @Override
        @Transactional
        public String explainInvoice(Long businessId, Long invoiceId) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

                if (!invoice.getBusiness().getBusinessId().equals(businessId)) {
                        log.warn("Unauthorized access attempt: Business {} tried to access invoice {} belonging to business {}",
                                        businessId, invoiceId, invoice.getBusiness().getBusinessId());
                        throw new RuntimeException("Unauthorized: This invoice does not belong to your business");
                }

                validateTokenLimit(businessId);
                log.info("AI invoice explanation for invoice {}", invoiceId);
                try {
                        String itemsList = invoice.getSale().getSaleItems().stream()
                                        .map(si -> si.getProduct().getProductName() + " (x" + si.getQty() + ")")
                                        .collect(Collectors.joining(", "));

                        String invoiceDetails = String.format(
                                        "Invoice #%s, Customer: %s, Date: %s, Amount: $%.2f. Details: %s",
                                        invoice.getInvoiceNumber(), invoice.getCustomerName(),
                                        invoice.getIssuedDate(), invoice.getSale().getTotalAmount(),
                                        itemsList);

                        String systemPrompt = "Explain this invoice clearly to a business owner, highlighting key details and any potential actions: ";
                        String result = chatModel.call(systemPrompt + invoiceDetails);
                        saveAiRequest(businessId, "Explain invoice: " + invoice.getInvoiceNumber(), result,
                                        "invoice_explanation");
                        return result;
                } catch (Exception e) {
                        log.error("Error explaining invoice via AI", e);
                        if (e.getMessage().contains("Token limit reached")) {
                                throw e;
                        }
                        return "Could not explain invoice via AI. Summary: Invoice #" + invoice.getInvoiceNumber()
                                        + " for "
                                        + invoice.getCustomerName() + " total: $" + invoice.getSale().getTotalAmount();
                }
        }
}
