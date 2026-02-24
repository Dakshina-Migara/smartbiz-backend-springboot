package com.SmartBiz.controller;

import com.SmartBiz.dto.AIRequestDto;
import com.SmartBiz.dto.BusinessesDto;
import com.SmartBiz.dto.SubscriptionPlanDto;
import com.SmartBiz.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminController - REST API Controller for Admin operations.
 *
 * @RestController combines @Controller + @ResponseBody, meaning:
 *                 - This class handles incoming HTTP requests
 *                 - All method return values are automatically converted to
 *                 JSON
 *
 *                 @RequestMapping("/api/v1/admin") sets the BASE URL for all
 *                 endpoints in this controller.
 *                 All endpoints below will start with:
 *                 http://localhost:8080/api/v1/admin/...
 *
 *                 Available Endpoints:
 *                 GET /api/v1/admin/businesses → List all businesses
 *                 GET /api/v1/admin/logs/ai → View all AI usage logs
 *                 GET /api/v1/admin/statistics → View system-wide statistics
 *                 POST /api/v1/admin/subscriptions/create → Create a new
 *                 subscription plan
 *                 PUT /api/v1/admin/subscriptions/{id} → Update an existing
 *                 plan
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    // Service layer dependency — contains the actual business logic
    private final AdminService adminService;

    /**
     * Constructor-based Dependency Injection.
     * Spring injects the AdminServiceImpl automatically (since it implements
     * AdminService).
     */
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * GET /api/v1/admin/businesses
     *
     * Retrieves a list of all businesses registered in the system.
     * ResponseEntity wraps the response with an HTTP status code (200 OK).
     *
     * @return ResponseEntity containing a List of BusinessesDto (JSON array)
     */
    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessesDto>> getAllBusinesses() {
        List<BusinessesDto> businesses = adminService.findAllBusinesses();
        return ResponseEntity.ok(businesses); // Returns HTTP 200 with the data
    }

    /**
     * GET /api/v1/admin/logs/ai
     *
     * Retrieves all AI request logs across the entire system.
     * Used by admins to monitor AI usage and token consumption.
     *
     * @return ResponseEntity containing a List of AIRequestDto (JSON array)
     */
    @GetMapping("/logs/ai")
    public ResponseEntity<List<AIRequestDto>> getAiUsageLogs() {
        List<AIRequestDto> logs = adminService.getGlobalAiLogs();
        return ResponseEntity.ok(logs); // Returns HTTP 200 with the AI logs
    }

    /**
     * PUT /api/v1/admin/subscriptions/{id}
     *
     * Updates an existing subscription plan by its ID.
     *
     * @PathVariable extracts the {id} from the URL path (e.g., /subscriptions/3 →
     *               id=3)
     * @Valid triggers bean validation on the incoming DTO (checks @NotNull, @Size,
     *        etc.)
     * @RequestBody deserializes the JSON request body into a SubscriptionPlanDto
     *              object
     *
     * @param id      the ID of the plan to update
     * @param planDto the new plan data from the request body
     * @return ResponseEntity containing the updated SubscriptionPlanDto
     */
    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionPlanDto> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {

        SubscriptionPlanDto updatedPlan = adminService.updateSubscriptionPlan(id, planDto);
        return ResponseEntity.ok(updatedPlan); // Returns HTTP 200 with the updated plan
    }

    /**
     * POST /api/v1/admin/subscriptions/create
     *
     * Creates a new subscription plan in the system.
     *
     * @Valid ensures the incoming data passes validation rules before processing.
     * @RequestBody converts the JSON request body to a SubscriptionPlanDto.
     *
     *              Returns HTTP 201 CREATED (instead of 200 OK) to indicate a new
     *              resource was created.
     *
     * @param planDto the new plan data from the request body
     * @return ResponseEntity containing the created SubscriptionPlanDto with HTTP
     *         201
     */
    @PostMapping("/subscriptions/create")
    public ResponseEntity<SubscriptionPlanDto> createPlan(
            @Valid @RequestBody SubscriptionPlanDto planDto) {

        SubscriptionPlanDto createdPlan = adminService.createSubscriptionPlan(planDto);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED); // Returns HTTP 201
    }

    /**
     * GET /api/v1/admin/statistics
     *
     * Returns system-wide statistics as a JSON object with key-value pairs:
     * - "totalBusinesses" → number of registered businesses
     * - "totalAiRequests" → number of AI requests made
     * - "totalTokensUsed" → total AI tokens consumed
     *
     * @return ResponseEntity containing a Map of statistics (JSON object)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> statistics = adminService.getSystemWideStatus();
        return ResponseEntity.ok(statistics); // Returns HTTP 200 with the stats
    }
}