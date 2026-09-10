package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.HotelRepository;
import com.santhosh.hotel_booking_management.service.HotelService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
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
    public Page<HotelResponseDTO> getAllHotels(Pageable pageable) {

        Page<Hotel> hotels = hotelRepository.findAll(pageable);
        return hotels.map(this :: mapToResponseDTO);
    }

    @Override
    public List<HotelResponseDTO> searchHotelsByLocation(String location) {
         List<Hotel> hotels = hotelRepository.findByLocation(location);
         List<HotelResponseDTO> responseList = new ArrayList<>();

         for(Hotel hotel : hotels){
             responseList.add(mapToResponseDTO(hotel));
         }
         return responseList;
    }

    @Override
    public List<HotelResponseDTO> searchHotelsByName(String name) {
        List<Hotel> hotels = hotelRepository.findByNameContaining(name);
        List<HotelResponseDTO> responseList = new ArrayList<>();

        for(Hotel hotel : hotels){
            responseList.add(mapToResponseDTO(hotel));
        }
        return responseList;
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
                        new ResourceNotFoundException("Hotel not found with id: " + id));

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
