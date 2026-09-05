package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.LoginRequestDTO;
import com.santhosh.hotel_booking_management.dto.request.RegisterRequestDTO;

public interface AuthService {
    void registerUser(RegisterRequestDTO registerRequestDTO);

    String loginUser(LoginRequestDTO loginRequestDTO);
}
