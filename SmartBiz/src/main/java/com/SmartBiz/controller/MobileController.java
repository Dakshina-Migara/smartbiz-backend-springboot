package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.service.BusinessOwnerService;
import com.SmartBiz.service.DashboardService;
import com.SmartBiz.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/{businessId}")
@RequiredArgsConstructor
public class MobileController {

    private final BusinessOwnerService businessOwnerService;
    private final InvoiceService invoiceService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMobileDashboard(@PathVariable @NonNull Long businessId) {
        Map<String, Object> kpis = dashboardService.getKPIs(businessId);
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("totalProducts", kpis.get("totalProducts"));
        data.put("totalSales", kpis.get("salesCount"));
        data.put("revenue", kpis.get("totalRevenue"));
        data.put("lowStockItems", kpis.get("lowStockAlerts"));

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getMobileInventory(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(businessOwnerService.getAllInventory(businessId), HttpStatus.OK);
    }

    @GetMapping("/inventory/search")
    public ResponseEntity<List<InventoryDto>> searchMobileInventory(@PathVariable @NonNull Long businessId,
            @RequestParam @NonNull String q) {
        return new ResponseEntity<>(businessOwnerService.searchInventory(businessId, q), HttpStatus.OK);
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addMobileInventory(@PathVariable @NonNull Long businessId,
            @Valid @RequestBody @NonNull InventoryDto dto) {
        dto.setBusinessId(businessId);
        return new ResponseEntity<>(businessOwnerService.addInventory(dto), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateMobileInventory(@PathVariable @NonNull Long businessId,
            @PathVariable @NonNull Long productId,
            @Valid @RequestBody @NonNull InventoryDto dto) {
        return new ResponseEntity<>(businessOwnerService.updateProduct(productId, dto, businessId), HttpStatus.OK);
    }

    @DeleteMapping("/inventory/{productId}")
    public ResponseEntity<Void> deleteMobileInventory(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long productId) {
        businessOwnerService.deleteProduct(productId, businessId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sales")
    public ResponseEntity<com.SmartBiz.dto.SalesDto> recordMobileSale(@PathVariable @NonNull Long businessId,
            @Valid @RequestBody @NonNull com.SmartBiz.dto.MobileSaleRequestDto dto) {
        return new ResponseEntity<>(businessOwnerService.recordMobileSale(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<com.SmartBiz.dto.SalesDto>> getMobileSalesHistory(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(businessOwnerService.getSalesHistory(businessId), HttpStatus.OK);
    }

    @GetMapping("/sales/{saleId}")
    public ResponseEntity<com.SmartBiz.dto.SalesDto> getMobileSaleDetails(@PathVariable @NonNull Long businessId, @PathVariable @NonNull Long saleId) {
        return new ResponseEntity<>(businessOwnerService.getSaleById(businessId, saleId), HttpStatus.OK);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<com.SmartBiz.dto.InvoiceDto>> getMobileInvoices(@PathVariable @NonNull Long businessId) {
        return new ResponseEntity<>(invoiceService.getAllInvoices(businessId), HttpStatus.OK);
    }
}
