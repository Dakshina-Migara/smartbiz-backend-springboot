package com.SmartBiz.service;

import com.SmartBiz.dto.CustomerDto;
import java.util.List;

public interface CustomerService {
    CustomerDto addCustomer(Long businessId, CustomerDto dto);

    List<CustomerDto> getAllCustomers(Long businessId);

    CustomerDto updateCustomer(Long businessId, Long customerId, CustomerDto dto);

    void deleteCustomer(Long businessId, Long customerId);
}
