package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.BookingRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.BookingResponseDTO;
import com.santhosh.hotel_booking_management.entity.Booking;
import com.santhosh.hotel_booking_management.entity.BookingStatus;
import com.santhosh.hotel_booking_management.entity.Room;
import com.santhosh.hotel_booking_management.entity.User;
import com.santhosh.hotel_booking_management.exception.BadRequestException;
import com.santhosh.hotel_booking_management.exception.ConflictException;
import com.santhosh.hotel_booking_management.exception.ForbiddenException;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.BookingRepository;
import com.santhosh.hotel_booking_management.repository.RoomRepository;
import com.santhosh.hotel_booking_management.repository.UserRepository;
import com.santhosh.hotel_booking_management.service.EmailService;
import com.santhosh.hotel_booking_management.service.BookingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
        private final BookingRepository bookingRepository;
        private final RoomRepository roomRepository;
        private final UserRepository userRepository;
        private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
                              UserRepository userRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
        LocalDate checkInDate = bookingRequestDTO.getCheckInDate();
        LocalDate checkOutDate = bookingRequestDTO.getCheckOutDate();
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new BadRequestException("Check-in date must be before check-out date");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "Check-in date cannot be in the past"
            );
        }
        Room room = roomRepository.findById(
                bookingRequestDTO.getRoomId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Room not found"));
        if(!room.getAvailable()){
            throw new ConflictException("Room is not available");
        }
        boolean roomAlreadyBooked = bookingRepository.
                existsByRoomIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        room.getId(), BookingStatus.CANCELLED,bookingRequestDTO.getCheckOutDate(),
                        bookingRequestDTO.getCheckInDate()
                );
        if(roomAlreadyBooked) {
            throw new ConflictException("Room is already booked for the selected dates");
        }

        long numberOfNights =
                ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(numberOfNights));
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);
        emailService.sendEmail(
                user.getEmail(),
                "Booking Confirmation",
                "Your booking has been confirmed successfully."
        );

        return mapToResponseDTO(savedBooking);
    }

    @Override
    public List<BookingResponseDTO> getMyBookings(String username) {
        List<Booking> bookings = bookingRepository.findByUserUsername(username);
        return bookings.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if(!booking.getUser().getUsername().equals(username)){
            throw new ForbiddenException("You are not authorized to view this booking");
        }
        return mapToResponseDTO(booking);
    }

    @Override
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new ForbiddenException(
                    "You are not authorized to cancel this booking"
            );
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException(
                    "Booking is already cancelled"
            );
        }
        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BadRequestException(
                    "Cannot cancel a booking after the check-in date"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
        emailService.sendEmail(
                booking.getUser().getEmail(),
                "Booking Cancellation",
                "Your booking has been cancelled successfully."
        );
    }
    private BookingResponseDTO mapToResponseDTO(Booking booking) {

        BookingResponseDTO responseDTO = new BookingResponseDTO();

        responseDTO.setId(booking.getId());
        responseDTO.setUserId(booking.getUser().getId());
        responseDTO.setUsername(booking.getUser().getUsername());
        responseDTO.setRoomId(booking.getRoom().getId());
        responseDTO.setRoomNumber(booking.getRoom().getRoomNumber());
        responseDTO.setCheckInDate(booking.getCheckInDate());
        responseDTO.setCheckOutDate(booking.getCheckOutDate());
        responseDTO.setTotalPrice(booking.getTotalPrice());
        responseDTO.setStatus(booking.getStatus());

        return responseDTO;
    }
}
