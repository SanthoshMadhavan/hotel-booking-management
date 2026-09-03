package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;

import java.util.List;

public interface HotelService {
   HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO);
   List<HotelResponseDTO> getAllHotels();
   HotelResponseDTO getHotelById(Long id);
   HotelResponseDTO updateHotel(Long id, HotelRequestDTO hotelRequestDTO);
   void deleteHotel(Long id);
}
