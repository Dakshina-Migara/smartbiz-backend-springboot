package com.SmartBiz.service.impl;

import com.SmartBiz.dto.PaymentDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Payment;
import com.SmartBiz.entity.Sales;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.PaymentRepository;
import com.SmartBiz.repository.SalesRepository;
import com.SmartBiz.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final BusinessRepository businessRepository;
    private final SalesRepository salesRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
            BusinessRepository businessRepository,
            SalesRepository salesRepository) {
        this.paymentRepository = paymentRepository;
        this.businessRepository = businessRepository;
        this.salesRepository = salesRepository;
    }

    @Override
    public PaymentDto recordPayment(Long businessId, PaymentDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            Sales sale = salesRepository.findById(dto.getSaleId())
                    .orElseThrow(() -> new RuntimeException("Sale not found with id: " + dto.getSaleId()));

            Payment payment = new Payment();
            payment.setPaymentMethod(dto.getPaymentMethod());
            payment.setAmount(dto.getAmount());
            payment.setSale(sale);
            payment.setBusiness(business);

            Payment saved = paymentRepository.save(payment);
            log.info("Recorded payment of {} for sale id: {}", dto.getAmount(), dto.getSaleId());
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error recording payment for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to record payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByBusiness(Long businessId) {
        try {
            return paymentRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching payments for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsBySale(Long saleId) {
        try {
            return paymentRepository.findBySaleId(saleId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching payments for sale id {}: {}", saleId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private PaymentDto mapToDto(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(p.getPaymentId());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setAmount(p.getAmount());
        dto.setPaymentDate(p.getPaymentDate());
        dto.setSaleId(p.getSale().getSaleId());
        dto.setBusinessId(p.getBusiness().getBusinessId());
        return dto;
    }
}
