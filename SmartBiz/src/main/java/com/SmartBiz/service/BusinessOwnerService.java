package com.SmartBiz.service;

import com.SmartBiz.dto.InventoryDto;
import com.SmartBiz.dto.SalesDto;

import java.util.List;

/**
 * BusinessOwnerService - Service Interface for Business Owner operations.
 *
 * Defines the contract for business-specific operations including:
 * - Inventory management (add, view, update stock)
 * - Sales management (record sales, view history)
 * - AI insights (generate business intelligence)
 *
 * The actual implementation is in BusinessOwnerServiceImpl.
 *
 * This interface follows the same pattern as AdminService — separating
 * the contract from the implementation for loose coupling and testability.
 */
public interface BusinessOwnerService {

    /**
     * Adds a new product to a business's inventory.
     * 
     * @param dto the InventoryDto containing product details and business_id
     * @return the created InventoryDto with the generated product ID
     */
    InventoryDto addInventory(InventoryDto dto);

    /**
     * Retrieves all inventory items belonging to a specific business.
     * 
     * @param businessId the ID of the business
     * @return List of InventoryDto containing all products for the business
     */
    List<InventoryDto> getAllInventory(Long businessId);

    /**
     * Updates the stock level of a specific product.
     * Also verifies that the product belongs to the specified business
     * (authorization check to prevent cross-business modifications).
     *
     * @param productId  the ID of the product to update
     * @param quantity   the new stock quantity
     * @param businessId the ID of the business (for authorization)
     * @return the updated InventoryDto
     */
    InventoryDto updateStock(Long productId, Integer quantity, Long businessId);

    /**
     * Records a new sales transaction for a business.
     * 
     * @param dto the SalesDto containing sale details and business_id
     * @return the created SalesDto with the generated sale ID
     */
    SalesDto recordSale(SalesDto dto);

    /**
     * Retrieves the complete sales history for a specific business,
     * sorted by date with the most recent sales first.
     * 
     * @param businessId the ID of the business
     * @return List of SalesDto sorted by sale date (newest first)
     */
    List<SalesDto> getSalesHistory(Long businessId);

    /**
     * Deletes a product from a business's inventory.
     *
     * @param productId  the ID of the product to delete
     * @param businessId the ID of the business (for authorization)
     */
    void deleteProduct(Long productId, Long businessId);

    /**
     * Generates AI-powered business insights based on inventory data.
     * Currently provides a simple low-stock analysis, but is designed
     * to be expanded with real AI integration (e.g., OpenAI).
     *
     * @param businessId the ID of the business to analyze
     * @param prompt     the user's prompt/question for the AI
     * @return a String containing the AI-generated insight
     */
    String generateAiInsight(Long businessId, String prompt);
}