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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

        private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);
        private final InventoryRepository inventoryRepository;
        private final SalesRepository salesRepository;
        private final InvoiceRepository invoiceRepository;

        @Override
        public String queryData(Long businessId, String prompt) {
                List<Inventory> inventory = inventoryRepository.findByBusinessId(businessId);
                List<Sales> sales = salesRepository.findByBusinessIdOrderBySaleDateDesc(businessId);

                long lowStockCount = inventory.stream().filter(i -> i.getStockLevel() < 5).count();
                double totalRevenue = sales.stream()
                                .mapToDouble(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0)
                                .sum();

                log.info("AI query for business {}: {}", businessId, prompt);
                return String.format("SmartBiz AI Insight:\n\n" +
                                "Based on your data, you have %d products in inventory, " +
                                "%d with low stock (below 5 units). Your total revenue across %d transactions is $%.2f.\n\n"
                                +
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
                                "[This is a placeholder. Connect OpenAI for professional AI-generated emails.]", prompt,
                                prompt);
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

                if (!invoice.getBusiness().getBusinessId().equals(businessId)) {
                        log.warn("Unauthorized access attempt: Business {} tried to access invoice {} belonging to business {}",
                                        businessId, invoiceId, invoice.getBusiness().getBusinessId());
                        throw new RuntimeException("Unauthorized: This invoice does not belong to your business");
                }

                log.info("AI invoice explanation for invoice {}", invoiceId);
                return String.format("Invoice Explanation:\n\n" +
                                "Invoice #%s was issued to %s.\n" +
                                "This invoice is linked to sale #%d.\n\n" +
                                "[This is a placeholder. Connect OpenAI for AI-powered invoice explanations.]",
                                invoice.getInvoiceNumber(), invoice.getCustomerName(), invoice.getSale().getSaleId());
        }
}
