package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.BookingRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String username);

    List<BookingResponseDTO> getMyBookings(String username);

    BookingResponseDTO getBookingById(Long bookingId, String username);

    void cancelBooking(Long bookingId, String username);
}
