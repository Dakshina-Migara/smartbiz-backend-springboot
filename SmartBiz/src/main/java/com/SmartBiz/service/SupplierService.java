package com.SmartBiz.service;

import com.SmartBiz.dto.SupplierDto;
import org.springframework.lang.NonNull;
import java.util.List;

public interface SupplierService {
    SupplierDto addSupplier(@NonNull Long businessId, @NonNull SupplierDto dto);

    List<SupplierDto> getAllSuppliers(@NonNull Long businessId);

    List<SupplierDto> searchSuppliers(@NonNull Long businessId, @NonNull String query);

    SupplierDto updateSupplier(@NonNull Long businessId, @NonNull Long supplierId, @NonNull SupplierDto dto);

    void deleteSupplier(@NonNull Long businessId, @NonNull Long supplierId);
}
