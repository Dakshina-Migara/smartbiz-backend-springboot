package com.SmartBiz.service;

import com.SmartBiz.dto.InvoiceDto;
import org.springframework.lang.NonNull;
import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(@NonNull Long businessId, @NonNull InvoiceDto dto);

    List<InvoiceDto> getAllInvoices(@NonNull Long businessId);

    InvoiceDto getInvoiceById(@NonNull Long businessId, @NonNull Long invoiceId);
}
