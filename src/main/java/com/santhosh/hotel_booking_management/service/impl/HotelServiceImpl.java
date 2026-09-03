package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.HotelRepository;
import com.santhosh.hotel_booking_management.service.HotelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO) {

        Hotel hotel = new Hotel();

        hotel.setName(hotelRequestDTO.getName());
        hotel.setLocation(hotelRequestDTO.getLocation());
        hotel.setDescription(hotelRequestDTO.getDescription());

        Hotel savedHotel = hotelRepository.save(hotel);

        return mapToResponseDTO(savedHotel);
    }

    @Override
    public List<HotelResponseDTO> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public HotelResponseDTO getHotelById(Long id) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + id));

        return mapToResponseDTO(hotel);
    }

    @Override
    public HotelResponseDTO updateHotel(Long id, HotelRequestDTO hotelRequestDTO) {

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + id));

        existingHotel.setName(hotelRequestDTO.getName());
        existingHotel.setLocation(hotelRequestDTO.getLocation());
        existingHotel.setDescription(hotelRequestDTO.getDescription());

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        return mapToResponseDTO(updatedHotel);
    }

    @Override
    public void deleteHotel(Long id) {

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Hotel not found with id: " + id));

        hotelRepository.delete(existingHotel);
    }

    private HotelResponseDTO mapToResponseDTO(Hotel hotel) {

        HotelResponseDTO responseDTO = new HotelResponseDTO();

        responseDTO.setId(hotel.getId());
        responseDTO.setName(hotel.getName());
        responseDTO.setLocation(hotel.getLocation());
        responseDTO.setDescription(hotel.getDescription());

        return responseDTO;
    }

}
