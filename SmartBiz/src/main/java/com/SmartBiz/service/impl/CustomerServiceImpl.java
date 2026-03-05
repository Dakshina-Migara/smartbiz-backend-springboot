package com.SmartBiz.service.impl;

import com.SmartBiz.dto.CustomerDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Customer;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.CustomerRepository;
import com.SmartBiz.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;

    @Override
    public CustomerDto addCustomer(Long businessId, CustomerDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            Customer customer = new Customer();
            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPhone(dto.getPhone());
            customer.setAddress(dto.getAddress());
            customer.setTotalPurchases(dto.getTotalPurchases() != null ? dto.getTotalPurchases() : 0.0);
            customer.setBusiness(business);

            Customer saved = customerRepository.save(customer);
            log.info("Added customer '{}' for business id: {}", dto.getName(), businessId);
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error adding customer for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to add customer: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers(Long businessId) {
        try {
            return customerRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching customers for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch customers: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomers(Long businessId, String query) {
        try {
            return customerRepository.searchByBusinessId(businessId, query)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching customers for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to search customers: " + e.getMessage());
        }
    }

    @Override
    public CustomerDto updateCustomer(Long businessId, Long customerId, CustomerDto dto) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

            if (!customer.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Customer does not belong to this business");
            }

            customer.setName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPhone(dto.getPhone());
            customer.setAddress(dto.getAddress());
            if (dto.getTotalPurchases() != null) {
                customer.setTotalPurchases(dto.getTotalPurchases());
            }

            Customer updated = customerRepository.save(customer);
            log.info("Updated customer id: {}", customerId);
            return mapToDto(updated);
        } catch (Exception e) {
            log.error("Error updating customer id {}: {}", customerId, e.getMessage(), e);
            throw new RuntimeException("Failed to update customer: " + e.getMessage());
        }
    }

    @Override
    public void deleteCustomer(Long businessId, Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

            if (!customer.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Customer does not belong to this business");
            }

            customerRepository.delete(customer);
            log.info("Deleted customer id: {}", customerId);
        } catch (Exception e) {
            log.error("Error deleting customer id {}: {}", customerId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete customer: " + e.getMessage());
        }
    }

    private CustomerDto mapToDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(c.getCustomerId());
        dto.setName(c.getName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setTotalPurchases(c.getTotalPurchases());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setBusinessId(c.getBusiness().getBusinessId());
        return dto;
    }
}
