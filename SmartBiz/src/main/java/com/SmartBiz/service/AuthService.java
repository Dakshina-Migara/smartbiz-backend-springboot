package com.SmartBiz.service;

import com.SmartBiz.dto.LoginDto;
import com.SmartBiz.dto.RegistrationDto;
import com.SmartBiz.dto.ResetPasswordDto;
import java.util.Map;

public interface AuthService {
    Map<String, Object> register(RegistrationDto dto);

    Map<String, Object> login(LoginDto dto);

    Map<String, Object> resetPassword(ResetPasswordDto dto);
}
