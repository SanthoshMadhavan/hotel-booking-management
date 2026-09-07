package com.santhosh.hotel_booking_management.repository;

import com.santhosh.hotel_booking_management.entity.Booking;
import com.santhosh.hotel_booking_management.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    boolean existsByRoomIdAndStatusNotAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate checkOutDate,
            LocalDate CheckInDate

    );
    List<Booking> findByUserUsername(String username);
}
