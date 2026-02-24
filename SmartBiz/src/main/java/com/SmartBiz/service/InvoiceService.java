package com.SmartBiz.service;

import com.SmartBiz.dto.InvoiceDto;
import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(Long businessId, InvoiceDto dto);

    List<InvoiceDto> getAllInvoices(Long businessId);

    InvoiceDto getInvoiceById(Long invoiceId);
}
