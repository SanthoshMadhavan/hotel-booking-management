package com.santhosh.hotel_booking_management.dto.response;

import com.santhosh.hotel_booking_management.entity.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long roomId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private BookingStatus status;
}
