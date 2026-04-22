package com.SmartBiz.service;

import com.SmartBiz.dto.CustomerDto;
import org.springframework.lang.NonNull;
import java.util.List;

public interface CustomerService {
    CustomerDto addCustomer(@NonNull Long businessId, @NonNull CustomerDto dto);

    List<CustomerDto> getAllCustomers(@NonNull Long businessId);

    List<CustomerDto> searchCustomers(@NonNull Long businessId, @NonNull String query);

    CustomerDto updateCustomer(@NonNull Long businessId, @NonNull Long customerId, @NonNull CustomerDto dto);

    void deleteCustomer(@NonNull Long businessId, @NonNull Long customerId);
}
