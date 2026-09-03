package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.RoomRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.RoomResponseDTO;
import com.santhosh.hotel_booking_management.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RoomService {
    RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO);

    List<RoomResponseDTO> getAllRooms();

    RoomResponseDTO getRoomById(Long id);

    RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO);

    void deleteRoom(Long id);

    List<RoomResponseDTO> filterRoomsByType(RoomType roomType);

    List<RoomResponseDTO> filterRoomsByMaxPrice(BigDecimal price);

    List<RoomResponseDTO> filterRoomsByAvailability(Boolean available);

    Page<RoomResponseDTO> filterRooms(RoomType roomType, Boolean available, BigDecimal price, Pageable pageable);
}
