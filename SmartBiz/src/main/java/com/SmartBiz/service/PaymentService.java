package com.SmartBiz.service;

import com.SmartBiz.dto.PaymentDto;
import java.util.List;

public interface PaymentService {
    PaymentDto recordPayment(Long businessId, PaymentDto dto);

    List<PaymentDto> getPaymentsByBusiness(Long businessId);

    List<PaymentDto> getPaymentsBySale(Long saleId);
}
