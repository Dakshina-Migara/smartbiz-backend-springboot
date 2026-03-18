package com.SmartBiz.controller;

import com.SmartBiz.dto.LoginDto;
import com.SmartBiz.dto.RegistrationDto;
import com.SmartBiz.dto.ResetPasswordDto;
import com.SmartBiz.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegistrationDto dto) {
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto dto) {
        return new ResponseEntity<>(authService.login(dto), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        return new ResponseEntity<>(authService.resetPassword(dto), HttpStatus.OK);
    }
}
