package com.SmartBiz.service;

import com.SmartBiz.dto.SupplierDto;
import java.util.List;

public interface SupplierService {
    SupplierDto addSupplier(Long businessId, SupplierDto dto);

    List<SupplierDto> getAllSuppliers(Long businessId);

    List<SupplierDto> searchSuppliers(Long businessId, String query);

    SupplierDto updateSupplier(Long businessId, Long supplierId, SupplierDto dto);

    void deleteSupplier(Long businessId, Long supplierId);
}
