package com.SmartBiz.service.impl;

import com.SmartBiz.dto.SupplierDto;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.entity.Supplier;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.repository.SupplierRepository;
import com.SmartBiz.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierServiceImpl.class);
    private final SupplierRepository supplierRepository;
    private final BusinessRepository businessRepository;

    @Override
    public SupplierDto addSupplier(@NonNull Long businessId, @NonNull SupplierDto dto) {
        try {
            Businesses business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new RuntimeException("Business not found with id: " + businessId));

            Supplier supplier = new Supplier();
            supplier.setName(dto.getName());
            supplier.setCompany(dto.getCompany());
            supplier.setEmail(dto.getEmail());
            supplier.setPhone(dto.getPhone());
            supplier.setAddress(dto.getAddress());
            supplier.setBusiness(business);

            Supplier saved = supplierRepository.save(supplier);
            log.info("Added supplier '{}' for business id: {}", dto.getName(), businessId);
            return mapToDto(saved);
        } catch (Exception e) {
            log.error("Error adding supplier for business id {}: {}", businessId, e.getMessage(), e);
            throw new RuntimeException("Failed to add supplier: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers(@NonNull Long businessId) {
        try {
            return supplierRepository.findByBusinessId(businessId)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching suppliers for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDto> searchSuppliers(@NonNull Long businessId, @NonNull String query) {
        try {
            return supplierRepository.searchByBusinessId(businessId, query)
                    .stream().map(this::mapToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching suppliers for business id {}: {}", businessId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public SupplierDto updateSupplier(@NonNull Long businessId, @NonNull Long supplierId, @NonNull SupplierDto dto) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

            if (!supplier.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Supplier does not belong to this business");
            }

            supplier.setName(dto.getName());
            supplier.setCompany(dto.getCompany());
            supplier.setEmail(dto.getEmail());
            supplier.setPhone(dto.getPhone());
            supplier.setAddress(dto.getAddress());

            Supplier updated = supplierRepository.save(supplier);
            log.info("Updated supplier id: {}", supplierId);
            return mapToDto(updated);
        } catch (Exception e) {
            log.error("Error updating supplier id {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Failed to update supplier: " + e.getMessage());
        }
    }

    @Override
    public void deleteSupplier(@NonNull Long businessId, @NonNull Long supplierId) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

            if (!supplier.getBusiness().getBusinessId().equals(businessId)) {
                throw new RuntimeException("Unauthorized: Supplier does not belong to this business");
            }

            supplierRepository.delete(supplier);
            log.info("Deleted supplier id: {}", supplierId);
        } catch (Exception e) {
            log.error("Error deleting supplier id {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete supplier: " + e.getMessage());
        }
    }

    private SupplierDto mapToDto(Supplier s) {
        SupplierDto dto = new SupplierDto();
        dto.setSupplierId(s.getSupplierId());
        dto.setName(s.getName());
        dto.setCompany(s.getCompany());
        dto.setEmail(s.getEmail());
        dto.setPhone(s.getPhone());
        dto.setAddress(s.getAddress());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setBusinessId(s.getBusiness().getBusinessId());
        return dto;
    }
}
