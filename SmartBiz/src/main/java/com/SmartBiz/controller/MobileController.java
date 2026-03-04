package com.SmartBiz.controller;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.service.BusinessOwnerService;
import com.SmartBiz.service.DashboardService;
import com.SmartBiz.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/{businessId}")
public class MobileController {

    private final BusinessOwnerService businessOwnerService;
    private final InvoiceService invoiceService;
    private final DashboardService dashboardService;

    @Autowired
    public MobileController(BusinessOwnerService businessOwnerService,
            InvoiceService invoiceService,
            DashboardService dashboardService) {
        this.businessOwnerService = businessOwnerService;
        this.invoiceService = invoiceService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMobileDashboard(@PathVariable Long businessId) {
        Map<String, Object> kpis = dashboardService.getKPIs(businessId);
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("totalProducts", kpis.get("totalProducts"));
        data.put("totalSales", kpis.get("salesCount"));
        data.put("revenue", kpis.get("totalRevenue"));
        data.put("lowStockItems", kpis.get("lowStockAlerts"));

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryDto>> getMobileInventory(@PathVariable Long businessId) {
        return new ResponseEntity<>(businessOwnerService.getAllInventory(businessId), HttpStatus.OK);
    }

    @GetMapping("/inventory/search")
    public ResponseEntity<List<InventoryDto>> searchMobileInventory(@PathVariable Long businessId,
            @RequestParam String q) {
        return new ResponseEntity<>(businessOwnerService.searchInventory(businessId, q), HttpStatus.OK);
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> addMobileInventory(@PathVariable Long businessId,
            @Valid @RequestBody InventoryDto dto) {
        dto.setBusinessId(businessId);
        return new ResponseEntity<>(businessOwnerService.addInventory(dto), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{productId}")
    public ResponseEntity<InventoryDto> updateMobileInventory(@PathVariable Long businessId,
            @PathVariable Long productId,
            @Valid @RequestBody InventoryDto dto) {
        return new ResponseEntity<>(businessOwnerService.updateProduct(productId, dto, businessId), HttpStatus.OK);
    }

    @DeleteMapping("/inventory/{productId}")
    public ResponseEntity<Void> deleteMobileInventory(@PathVariable Long businessId, @PathVariable Long productId) {
        businessOwnerService.deleteProduct(productId, businessId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sales")
    public ResponseEntity<com.SmartBiz.dto.SalesDto> recordMobileSale(@PathVariable Long businessId,
            @Valid @RequestBody com.SmartBiz.dto.MobileSaleRequestDto dto) {
        return new ResponseEntity<>(businessOwnerService.recordMobileSale(businessId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<com.SmartBiz.dto.SalesDto>> getMobileSalesHistory(@PathVariable Long businessId) {
        return new ResponseEntity<>(businessOwnerService.getSalesHistory(businessId), HttpStatus.OK);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<com.SmartBiz.dto.InvoiceDto>> getMobileInvoices(@PathVariable Long businessId) {
        return new ResponseEntity<>(invoiceService.getAllInvoices(businessId), HttpStatus.OK);
    }
}
