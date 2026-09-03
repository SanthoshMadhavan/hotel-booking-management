package com.santhosh.hotel_booking_management.repository;

import com.santhosh.hotel_booking_management.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
      List<Hotel> findByLocation(String location);

    List<Hotel> findByNameContaining(String name);
}
