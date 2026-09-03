package com.santhosh.hotel_booking_management.repository;

import com.santhosh.hotel_booking_management.entity.Room;
import com.santhosh.hotel_booking_management.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room,Long> {
        List<Room> findByRoomType(RoomType roomType);

        List<Room> findByPriceLessThanEqual(BigDecimal price);

        List<Room> findByAvailable(Boolean available);

        Page<Room> findByRoomTypeAndAvailableAndPriceLessThanEqual(
                RoomType roomType,
                Boolean available,
                BigDecimal price,
                Pageable pageable
        );
}
