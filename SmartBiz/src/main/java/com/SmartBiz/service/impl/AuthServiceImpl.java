package com.SmartBiz.service.impl;

import com.SmartBiz.dto.LoginDto;
import com.SmartBiz.dto.RegistrationDto;
import com.SmartBiz.entity.Admin;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.exception.ResourceNotFoundException;
import com.SmartBiz.repository.AdminRepository;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final BusinessRepository businessRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AuthServiceImpl(BusinessRepository businessRepository, AdminRepository adminRepository) {
        this.businessRepository = businessRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public Map<String, Object> register(RegistrationDto dto) {
        try {
            if (adminRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered: " + dto.getEmail());
            }

            // 1. Create Business
            Businesses business = new Businesses();
            business.setName(dto.getBusinessName());
            business.setAddress(dto.getBusinessAddress());
            business.setEmail(dto.getEmail());
            business.setPhone(dto.getPhone());
            business.setBusinessOwnerName(dto.getOwnerName());
            business.setStatus("active");
            business.setRegisteredDate(LocalDateTime.now());

            Businesses savedBusiness = businessRepository.save(business);

            // 2. Create Admin account
            Admin admin = new Admin();
            admin.setName(dto.getOwnerName());
            admin.setEmail(dto.getEmail());
            admin.setPassword(dto.getPassword()); // In production, hash this!
            admin.setRole(dto.getRole() != null ? dto.getRole().toUpperCase() : "OWNER");
            admin.setBusiness(savedBusiness);
            admin.setCreatedAt(LocalDateTime.now());

            adminRepository.save(admin);

            log.info("Registered new business: {} for owner: {}", business.getName(), admin.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("businessId", savedBusiness.getBusinessId());
            response.put("ownerEmail", admin.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> login(LoginDto dto) {
        try {
            Admin admin = adminRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

            // Simple password check (No hashing for now as per user pattern)
            if (!admin.getPassword().equals(dto.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("adminId", admin.getAdminId());
            response.put("role", admin.getRole());

            if (admin.getBusiness() != null) {
                response.put("businessId", admin.getBusiness().getBusinessId());
                response.put("businessName", admin.getBusiness().getName());
            }

            log.info("User logged in: {}", admin.getEmail());
            return response;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}
