package com.SmartBiz.service.impl;

import com.SmartBiz.entity.Inventory;
import com.SmartBiz.entity.Invoice;
import com.SmartBiz.entity.Sales;
import com.SmartBiz.repository.InventoryRepository;
import com.SmartBiz.repository.InvoiceRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AiServiceImpl - Placeholder implementation for AI-powered features.
 *
 * Currently returns simulated AI responses based on real business data.
 * When you integrate OpenAI, replace the return statements with actual API
 * calls.
 *
 * To integrate OpenAI:
 * 1. Uncomment the spring-ai-starter-model-openai-sdk dependency in pom.xml
 * 2. Add your API key to application.properties:
 * spring.ai.openai.api-key=your-key
 * 3. Inject ChatClient and replace the placeholder responses
 */
@Service
@Transactional(readOnly = true)
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
    private final InventoryRepository inventoryRepository;
    private final SalesRepository salesRepository;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public AiServiceImpl(InventoryRepository inventoryRepository,
            SalesRepository salesRepository,
            InvoiceRepository invoiceRepository) {
        this.inventoryRepository = inventoryRepository;
        this.salesRepository = salesRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public String queryData(Long businessId, String prompt) {
        List<Inventory> inventory = inventoryRepository.findByBusinessId(businessId);
        List<Sales> sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);

        long lowStockCount = inventory.stream().filter(i -> i.getStockLevel() < 5).count();
        double totalRevenue = sales.stream().mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                .sum();

        // Placeholder response — will be replaced with actual OpenAI call
        log.info("AI query for business {}: {}", businessId, prompt);
        return String.format("SmartBiz AI Insight:\n\n" +
                "Based on your data, you have %d products in inventory, " +
                "%d with low stock (below 5 units). Your total revenue across %d transactions is $%.2f.\n\n" +
                "Your query: \"%s\"\n\n" +
                "[This is a placeholder response. Connect OpenAI for real insights.]",
                inventory.size(), lowStockCount, sales.size(), totalRevenue, prompt);
    }

    @Override
    public String generateEmail(Long businessId, String prompt) {
        log.info("AI email generation for business {}: {}", businessId, prompt);
        return String.format("Subject: Re: %s\n\n" +
                "Dear valued partner,\n\n" +
                "Thank you for reaching out to us. %s\n\n" +
                "We appreciate your business and look forward to continuing our partnership.\n\n" +
                "Best regards,\nSmartBiz Team\n\n" +
                "[This is a placeholder. Connect OpenAI for professional AI-generated emails.]", prompt, prompt);
    }

    @Override
    public String generatePost(Long businessId, String prompt) {
        log.info("AI social media post for business {}: {}", businessId, prompt);
        return String.format("🎉 Exciting news from our store!\n\n" +
                "%s\n\n" +
                "Visit us today and discover amazing deals! 🛒✨\n" +
                "#SmartBiz #ShopLocal #NewArrivals\n\n" +
                "[This is a placeholder. Connect OpenAI for AI-generated social posts.]", prompt);
    }

    @Override
    public String explainInvoice(Long businessId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        log.info("AI invoice explanation for invoice {}", invoiceId);
        return String.format("Invoice Explanation:\n\n" +
                "Invoice #%s was issued to %s.\n" +
                "This invoice is linked to sale #%d.\n\n" +
                "[This is a placeholder. Connect OpenAI for AI-powered invoice explanations.]",
                invoice.getInvoiceNumber(), invoice.getCustomerName(), invoice.getSale().getSaleId());
    }
}
