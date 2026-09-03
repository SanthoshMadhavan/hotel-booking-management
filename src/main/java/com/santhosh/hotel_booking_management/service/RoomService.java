package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.RoomRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.RoomResponseDTO;

import java.util.List;

public interface RoomService {
    RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO);
    List<RoomResponseDTO> getAllRooms();
    RoomResponseDTO getRoomById(Long id);
    RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO);
    void deleteRoom(Long id);
}
