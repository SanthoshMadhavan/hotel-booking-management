package com.santhosh.hotel_booking_management.service;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface HotelService {
   HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO);

   Page<HotelResponseDTO> getAllHotels(Pageable pageable);

   List<HotelResponseDTO> searchHotelsByLocation(String location);

   List<HotelResponseDTO> searchHotelsByName(String name);

   HotelResponseDTO getHotelById(Long id);

   HotelResponseDTO updateHotel(Long id, HotelRequestDTO hotelRequestDTO);

   void deleteHotel(Long id);
}
