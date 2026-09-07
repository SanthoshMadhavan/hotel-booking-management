package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger =
            LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch(Exception e){
            logger.error("Failed to send email to: {}", to, e);
        }
    }
}
