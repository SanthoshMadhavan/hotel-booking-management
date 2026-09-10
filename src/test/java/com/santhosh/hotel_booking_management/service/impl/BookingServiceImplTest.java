package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.BookingRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.BookingResponseDTO;
import com.santhosh.hotel_booking_management.entity.*;
import com.santhosh.hotel_booking_management.exception.BadRequestException;
import com.santhosh.hotel_booking_management.exception.ConflictException;
import com.santhosh.hotel_booking_management.exception.ForbiddenException;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.BookingRepository;
import com.santhosh.hotel_booking_management.repository.RoomRepository;
import com.santhosh.hotel_booking_management.repository.UserRepository;
import com.santhosh.hotel_booking_management.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                roomRepository,
                userRepository,
                emailService
        );
    }
    @Test
    void testCreateBooking() {
        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(5));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(7));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(new BigDecimal("2000.00"));
        room.setAvailable(true);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));
        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));
        when(bookingRepository
                .existsByRoomIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        1L,
                        BookingStatus.CANCELLED,
                        requestDTO.getCheckOutDate(),
                        requestDTO.getCheckInDate()
                ))
                .thenReturn(false);

        Booking savedBooking = new Booking();

        savedBooking.setId(1L);
        savedBooking.setUser(user);
        savedBooking.setRoom(room);
        savedBooking.setCheckInDate(requestDTO.getCheckInDate());
        savedBooking.setCheckOutDate(requestDTO.getCheckOutDate());
        savedBooking.setTotalPrice(new BigDecimal("4000.00"));
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);
        BookingResponseDTO response =
                bookingService.createBooking(requestDTO, "santhosh");
        verify(emailService).sendEmail(
                "santhosh@gmail.com",
                "Booking Confirmation",
                "Your booking has been confirmed successfully."
        );
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("santhosh", response.getUsername());
        assertEquals(1L, response.getRoomId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(requestDTO.getCheckInDate(), response.getCheckInDate());
        assertEquals(requestDTO.getCheckOutDate(), response.getCheckOutDate());
        assertEquals(new BigDecimal("4000.00"), response.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }
    @Test
    void testCreateBooking_UserNotFound() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(5));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(7));

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.createBooking(requestDTO, "unknown")
        );

        verify(userRepository).findByUsername("unknown");
    }
    @Test
    void testCreateBooking_InvalidDates() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(7));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(5));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(requestDTO, "santhosh")
        );

        verify(userRepository).findByUsername("santhosh");
    }
    @Test
    void testCreateBooking_PastCheckInDate() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().minusDays(2));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(2));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(requestDTO, "santhosh")
        );

        verify(userRepository).findByUsername("santhosh");
    }
    @Test
    void testCreateBooking_RoomNotFound() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(5));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(7));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.createBooking(requestDTO, "santhosh")
        );

        verify(userRepository).findByUsername("santhosh");
        verify(roomRepository).findById(1L);
    }
    @Test
    void testCreateBooking_RoomUnavailable() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(5));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(7));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(new BigDecimal("2000.00"));
        room.setAvailable(false);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                ConflictException.class,
                () -> bookingService.createBooking(requestDTO, "santhosh")
        );

        verify(userRepository).findByUsername("santhosh");
        verify(roomRepository).findById(1L);
    }
    @Test
    void testCreateBooking_RoomAlreadyBooked() {

        BookingRequestDTO requestDTO = new BookingRequestDTO();

        requestDTO.setRoomId(1L);
        requestDTO.setCheckInDate(LocalDate.now().plusDays(5));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(7));

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(new BigDecimal("2000.00"));
        room.setAvailable(true);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        1L,
                        BookingStatus.CANCELLED,
                        requestDTO.getCheckOutDate(),
                        requestDTO.getCheckInDate()
                ))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> bookingService.createBooking(requestDTO, "santhosh")
        );

        verify(userRepository).findByUsername("santhosh");
        verify(roomRepository).findById(1L);
        verify(bookingRepository)
                .existsByRoomIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        1L,
                        BookingStatus.CANCELLED,
                        requestDTO.getCheckOutDate(),
                        requestDTO.getCheckInDate()
                );
    }
    @Test
    void testGetMyBookings() {
        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findByUserUsername("santhosh"))
                .thenReturn(List.of(booking));
        List<BookingResponseDTO> response =
                bookingService.getMyBookings("santhosh");
        assertEquals(1, response.size());

        BookingResponseDTO bookingResponse = response.get(0);

        assertEquals(1L, bookingResponse.getId());
        assertEquals(1L, bookingResponse.getUserId());
        assertEquals("santhosh", bookingResponse.getUsername());
        assertEquals(1L, bookingResponse.getRoomId());
        assertEquals("101", bookingResponse.getRoomNumber());
        assertEquals(booking.getCheckInDate(), bookingResponse.getCheckInDate());
        assertEquals(booking.getCheckOutDate(), bookingResponse.getCheckOutDate());
        assertEquals(new BigDecimal("4000.00"), bookingResponse.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, bookingResponse.getStatus());
        verify(bookingRepository).findByUserUsername("santhosh");
    }
    @Test
    void testGetBookingById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        BookingResponseDTO response =
                bookingService.getBookingById(1L, "santhosh");
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("santhosh", response.getUsername());
        assertEquals(1L, response.getRoomId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(booking.getCheckInDate(), response.getCheckInDate());
        assertEquals(booking.getCheckOutDate(), response.getCheckOutDate());
        assertEquals(new BigDecimal("4000.00"), response.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        verify(bookingRepository).findById(1L);
    }
    @Test
    void testGetBookingByIdUnauthorized() {

        User user = new User();
        user.setId(1L);
        user.setUsername("otheruser");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        assertThrows(
                ForbiddenException.class,
                () -> bookingService.getBookingById(1L, "santhosh")
        );
        verify(bookingRepository).findById(1L);
    }
    @Test
    void testCancelBooking() {

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        bookingService.cancelBooking(1L, "santhosh");
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(booking);
        verify(emailService).sendEmail(
                "santhosh@gmail.com",
                "Booking Cancellation",
                "Your booking has been cancelled successfully."
        );
    }
    @Test
    void testCancelBookingUnauthorized() {

        User user = new User();
        user.setId(1L);
        user.setUsername("otheruser");
        user.setEmail("other@gmail.com");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        assertThrows(
                ForbiddenException.class,
                () -> bookingService.cancelBooking(1L, "santhosh")
        );
        verify(bookingRepository).findById(1L);
    }
    @Test
    void testCancelBookingAlreadyCancelled() {

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().plusDays(5));
        booking.setCheckOutDate(LocalDate.now().plusDays(7));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        assertThrows(
                ConflictException.class,
                () -> bookingService.cancelBooking(1L, "santhosh")
        );
        verify(bookingRepository).findById(1L);
    }
    @Test
    void testCancelBookingAfterCheckInDate() {

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.now().minusDays(2));
        booking.setCheckOutDate(LocalDate.now().plusDays(2));
        booking.setTotalPrice(new BigDecimal("4000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        assertThrows(
                BadRequestException.class,
                () -> bookingService.cancelBooking(1L, "santhosh")
        );
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(emailService, never()).sendEmail(
                anyString(),
                anyString(),
                anyString()
        );
    }
}
