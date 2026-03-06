package com.SmartBiz.service.impl;

import com.SmartBiz.dto.LoginDto;
import com.SmartBiz.dto.RegistrationDto;
import com.SmartBiz.entity.Admin;
import com.SmartBiz.entity.Businesses;
import com.SmartBiz.exception.ResourceNotFoundException;
import com.SmartBiz.repository.AdminRepository;
import com.SmartBiz.repository.BusinessRepository;
import com.SmartBiz.security.JwtService;
import com.SmartBiz.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final BusinessRepository businessRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public Map<String, Object> register(RegistrationDto dto) {
        try {
            if (adminRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already registered: " + dto.getEmail());
            }

            // 1. Determine Role
            String roleStr = dto.getRole() != null ? dto.getRole().toUpperCase() : "OWNER";

            // 2. Create Admin account object
            Admin admin = new Admin();
            admin.setName(dto.getOwnerName());
            admin.setEmail(dto.getEmail());
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
            admin.setRole(roleStr);
            admin.setCreatedAt(LocalDateTime.now());

            if ("OWNER".equalsIgnoreCase(roleStr)) {
                // Create Business only for Owners
                Businesses business = new Businesses();
                business.setName(dto.getBusinessName());
                business.setAddress(dto.getBusinessAddress());
                business.setEmail(dto.getEmail());
                business.setPhone(dto.getPhone());
                business.setBusinessOwnerName(dto.getOwnerName());
                business.setStatus("active");
                business.setRegisteredDate(LocalDateTime.now());

                Businesses savedBusiness = businessRepository.save(business);
                admin.setBusiness(savedBusiness);
                log.info("Registered new business: {} for owner: {}", business.getName(), admin.getEmail());
            } else {
                log.info("Registered new Admin: {}", admin.getEmail());
            }

            adminRepository.save(admin);

            String jwtToken = jwtService.generateToken(admin);
            log.info("Generated token for registration: {}", jwtToken);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("token", jwtToken);
            response.put("expiresIn", jwtService.getExpirationTime());
            response.put("tokenType", "Bearer");
            response.put("businessId", savedBusiness.getBusinessId());
            response.put("ownerEmail", admin.getEmail());
            response.put("role", admin.getRole());

            if ("ADMIN".equalsIgnoreCase(admin.getRole())) {
                response.put("accessibleArea", "ADMIN_PORTAL");
                response.put("homePath", "/admin/dashboard");
            } else {
                response.put("accessibleArea", "BUSINESS_PORTAL");
                response.put("homePath", "/owner/dashboard");
            }

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
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()));

            Admin admin = adminRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

            String jwtToken = jwtService.generateToken(admin);
            log.info("Generated token for login: {}", jwtToken);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", jwtToken);
            response.put("expiresIn", 3600000); // 1 hour in ms
            response.put("tokenType", "Bearer");
            response.put("adminId", admin.getAdminId());
            response.put("role", admin.getRole());

            if ("ADMIN".equalsIgnoreCase(admin.getRole())) {
                response.put("accessibleArea", "ADMIN_PORTAL");
                response.put("homePath", "/admin/dashboard");
            } else {
                response.put("accessibleArea", "BUSINESS_PORTAL");
                response.put("homePath", "/owner/dashboard");
            }

            if (admin.getBusiness() != null) {
                response.put("businessId", admin.getBusiness().getBusinessId());
                response.put("businessName", admin.getBusiness().getName());
            }

            log.info("User {} logged in as {}", admin.getEmail(), admin.getRole());
            return response;

        } catch (AuthenticationException e) {
            log.error("Login error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected login error: {}", e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}
