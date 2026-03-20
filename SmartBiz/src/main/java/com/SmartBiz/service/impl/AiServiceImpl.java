package com.SmartBiz.service.impl;

import com.SmartBiz.entity.Inventory;
import com.SmartBiz.entity.Invoice;
import com.SmartBiz.entity.Sales;
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
        private final ChatModel chatModel;

        @Override
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
                                                "Current Business Snapshot: %d products in inventory, %d with low stock (below 5 units). " +
                                                "Total revenue from %d transactions: $%.2f. " +
                                                "Top Inventory Items: %s. " +
                                                "Provide a professional and helpful insight based on the user's query.",
                                inventory.size(), lowStockCount, sales.size(), totalRevenue, topItems);

                log.info("AI query for business {}: {}", businessId, prompt);
                try {
                        return chatModel.call(systemContext + "\nUser Query: " + prompt);
                } catch (Exception e) {
                        log.error("Error calling AI service", e);
                        return "I'm sorry, I'm having trouble connecting to the AI service right now. " +
                                        "Summary: You have " + inventory.size() + " products and revenue of $" + totalRevenue;
                }
        }

        @Override
        public String generateEmail(Long businessId, String prompt) {
                log.info("AI email generation for business {}: {}", businessId, prompt);
                try {
                        String systemPrompt = "You are an expert business communicator. Generate a professional email based on the following request: ";
                        return chatModel.call(systemPrompt + prompt);
                } catch (Exception e) {
                        log.error("Error generating AI email", e);
                        return "Failed to generate email via AI. Original request: " + prompt;
                }
        }

        @Override
        public String generatePost(Long businessId, String prompt) {
                log.info("AI social media post for business {}: {}", businessId, prompt);
                try {
                        String systemPrompt = "You are a social media expert. Generate an engaging post with relevant emojis and hashtags based on: ";
                        return chatModel.call(systemPrompt + prompt);
                } catch (Exception e) {
                        log.error("Error generating AI post", e);
                        return "Failed to generate social media post via AI. Original request: " + prompt;
                }
        }

        @Override
        public String explainInvoice(Long businessId, Long invoiceId) {
                Invoice invoice = invoiceRepository.findById(invoiceId)
                                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

                if (!invoice.getBusiness().getBusinessId().equals(businessId)) {
                        log.warn("Unauthorized access attempt: Business {} tried to access invoice {} belonging to business {}",
                                        businessId, invoiceId, invoice.getBusiness().getBusinessId());
                        throw new RuntimeException("Unauthorized: This invoice does not belong to your business");
                }

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
                        return chatModel.call(systemPrompt + invoiceDetails);
                } catch (Exception e) {
                        log.error("Error explaining invoice via AI", e);
                        return "Could not explain invoice via AI. Summary: Invoice #" + invoice.getInvoiceNumber() + " for "
                                        + invoice.getCustomerName() + " total: $" + invoice.getSale().getTotalAmount();
                }
        }
}
