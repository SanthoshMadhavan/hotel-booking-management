package com.santhosh.hotel_booking_management.repository;

import com.santhosh.hotel_booking_management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {

}
