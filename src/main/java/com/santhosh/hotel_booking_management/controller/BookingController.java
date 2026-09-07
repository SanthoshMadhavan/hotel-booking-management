package com.santhosh.hotel_booking_management.controller;

import com.santhosh.hotel_booking_management.dto.request.BookingRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.BookingResponseDTO;
import com.santhosh.hotel_booking_management.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
      private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO bookingRequestDTO,
            Authentication authentication
            ){
        String username = authentication.getName();
        System.out.println("Logged-in username: " + username);
        BookingResponseDTO response = bookingService.createBooking(bookingRequestDTO,username);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            Authentication authentication) {

        String username = authentication.getName();

        List<BookingResponseDTO> bookings =
                bookingService.getMyBookings(username);

        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {

        String username = authentication.getName();

        BookingResponseDTO booking =
                bookingService.getBookingById(
                        bookingId,
                        username
                );

        return ResponseEntity.ok(booking);
    }
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        String username = authentication.getName();

        bookingService.cancelBooking(
                bookingId,
                username
        );

        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
