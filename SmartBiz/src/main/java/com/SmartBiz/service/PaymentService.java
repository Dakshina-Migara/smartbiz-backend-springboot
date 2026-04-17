package com.SmartBiz.service;

import com.SmartBiz.dto.PaymentDto;
import org.springframework.lang.NonNull;
import java.util.List;

public interface PaymentService {
    PaymentDto recordPayment(@NonNull Long businessId, @NonNull PaymentDto dto);

    List<PaymentDto> getPaymentsByBusiness(@NonNull Long businessId);

    List<PaymentDto> getPaymentsBySale(@NonNull Long saleId);
}
