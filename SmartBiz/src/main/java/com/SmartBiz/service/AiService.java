package com.SmartBiz.service;

public interface AiService {
    String queryData(Long businessId, String prompt);

    String generateEmail(Long businessId, String prompt);

    String generatePost(Long businessId, String prompt);

    String explainInvoice(Long businessId, Long invoiceId);
}
