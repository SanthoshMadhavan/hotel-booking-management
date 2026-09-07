package com.santhosh.hotel_booking_management.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
