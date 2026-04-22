package com.SmartBiz.service;

import org.springframework.lang.NonNull;

public interface AiService {
    String queryData(@NonNull Long businessId, @NonNull String prompt);

    String generateEmail(@NonNull Long businessId, @NonNull String prompt);

    String generatePost(@NonNull Long businessId, @NonNull String prompt);

    String explainInvoice(@NonNull Long businessId, @NonNull Long invoiceId);
}
