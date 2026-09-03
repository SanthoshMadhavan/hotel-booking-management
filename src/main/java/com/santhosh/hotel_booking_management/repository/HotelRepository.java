package com.santhosh.hotel_booking_management.repository;

import com.santhosh.hotel_booking_management.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {

}
