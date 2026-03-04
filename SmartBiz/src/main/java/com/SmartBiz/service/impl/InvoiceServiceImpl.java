package com.SmartBiz.service.impl;

import com.SmartBiz.dto.InvoiceDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Invoice;
import com.SmartBiz.entity.Sales;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.InvoiceRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final SalesRepository salesRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
            BusinessRepository businessRepository,
            SalesRepository salesRepository) {
        this.invoiceRepository = invoiceRepository;
        this.businessRepository = businessRepository;
        this.salesRepository = salesRepository;
    }

    @Override
    public InvoiceDto createInvoice(Long businessId, InvoiceDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            Sales sale = salesRepository.findById(dto.getSaleId())
                    .orElseThrow(() -> new RuntimeException("Sale not found with id: " + dto.getSaleId()));

            if (!sale.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Sale does not belong to this business");
            }

            Invoice invoice = new Invoice();

            // Improved unique invoice number generation
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uniqueRef = String.valueOf(System.currentTimeMillis() % 10000);
            invoice.setInvoiceNumber("INV-" + dateStr + "-" + uniqueRef);

            invoice.setCustomerName(dto.getCustomerName());
            invoice.setCustomerEmail(dto.getCustomerEmail());
            invoice.setSale(sale);
            invoice.setBusiness(business);

            Invoice saved = invoiceRepository.save(invoice);
            log.info("Created invoice {} for business id: {}", saved.getInvoiceNumber(), businessId);
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error creating invoice for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to create invoice: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices(Long businessId) {
        try {
            return invoiceRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching invoices for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long businessId, Long invoiceId) {
        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

            if (!invoice.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Invoice does not belong to this business");
            }

            return mapToDto(invoice);
        } catch (Exception e) {
            log.error("Error fetching invoice id {} for business {}: {}", invoiceId, businessId, e.getMessage());
            throw new RuntimeException("Failed to fetch invoice: " + e.getMessage());
        }
    }

    private InvoiceDto mapToDto(Invoice i) {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(i.getInvoiceId());
        dto.setInvoiceNumber(i.getInvoiceNumber());
        dto.setCustomerName(i.getCustomerName());
        dto.setCustomerEmail(i.getCustomerEmail());
        dto.setIssuedDate(i.getIssuedDate());
        dto.setCreatedAt(i.getCreatedAt());
        dto.setSaleId(i.getSale().getSaleId());
        dto.setBusinessId(i.getBusiness().getBusinessId());
        return dto;
    }
}
