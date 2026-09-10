package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    private HotelServiceImpl hotelService;

    @BeforeEach
    void setUp() {
        hotelService = new HotelServiceImpl(hotelRepository);
    }

    @Test
    void testCreateHotel(){
        HotelRequestDTO requestDTO = new HotelRequestDTO();
        requestDTO.setName("Taj Hotel");
        requestDTO.setLocation("Hyderabad");
        requestDTO.setDescription("Luxury hotel");

        Hotel savedHotel = new Hotel();
        savedHotel.setId(1L);
        savedHotel.setName("Taj Hotel");
        savedHotel.setLocation("Hyderabad");
        savedHotel.setDescription("Luxury hotel");

        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelResponseDTO response = hotelService.createHotel(requestDTO);

        assertEquals(1L, response.getId());
        assertEquals("Taj Hotel", response.getName());
        assertEquals("Hyderabad", response.getLocation());
        assertEquals("Luxury hotel", response.getDescription());

        verify(hotelRepository).save(any(Hotel.class));
    }
    @Test
    void testGetHotelById(){
            Hotel hotel = new Hotel();
             hotel.setId(1L);
             hotel.setName("Taj Hotel");
             hotel.setLocation("Hyderabad");
             hotel.setDescription("Luxury hotel");

             when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

            HotelResponseDTO response = hotelService.getHotelById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Taj Hotel", response.getName());
        assertEquals("Hyderabad", response.getLocation());
        assertEquals("Luxury hotel", response.getDescription());
    }
    @Test
    void testGetHotelById_NotFound(){
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> hotelService.getHotelById(999L)
        );
    }
    @Test
    void testSearchHotelsByLocation(){
        Hotel hotel1 = new Hotel();
        hotel1.setId(1L);
        hotel1.setName("Taj Hotel");
        hotel1.setLocation("Hyderabad");
        hotel1.setDescription("Luxury hotel");

        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("ITC Hotel");
        hotel2.setLocation("Hyderabad");
        hotel2.setDescription("Business hotel");
        List<Hotel> hotels = List.of(hotel1,hotel2);

        when(hotelRepository.findByLocation("Hyderabad")).thenReturn(hotels);

        List<HotelResponseDTO> responseList = hotelService.searchHotelsByLocation("Hyderabad");

        assertEquals(2,responseList.size());
        assertEquals(1L,responseList.get(0).getId());
        assertEquals("Taj Hotel",responseList.get(0).getName());
        assertEquals(2L, responseList.get(1).getId());
        assertEquals("ITC Hotel", responseList.get(1).getName());
        verify(hotelRepository).findByLocation("Hyderabad");
    }
    @Test
    void testSearchHotelsByName(){
        Hotel hotel1 = new Hotel();
        hotel1.setId(1L);
        hotel1.setName("Taj Hotel");
        hotel1.setLocation("Hyderabad");
        hotel1.setDescription("Luxury hotel");

        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("ITC Hotel");
        hotel2.setLocation("Chennai");
        hotel2.setDescription("Business hotel");
        List<Hotel> hotels = List.of(hotel1,hotel2);

        when(hotelRepository.findByNameContaining("Hotel")).thenReturn(hotels);

        List<HotelResponseDTO> responseList = hotelService.searchHotelsByName("Hotel");

        assertEquals(2, responseList.size());
        assertEquals(1L, responseList.get(0).getId());
        assertEquals("Taj Hotel", responseList.get(0).getName());
        assertEquals(2L, responseList.get(1).getId());
        assertEquals("ITC Hotel", responseList.get(1).getName());
        verify(hotelRepository).findByNameContaining("Hotel");
    }
    @Test
    void testGetAllHotels(){
        Hotel hotel = new Hotel();

        hotel.setId(1L);
        hotel.setName("Taj Hotel");
        hotel.setLocation("Hyderabad");
        hotel.setDescription("Luxury hotel");
        List<Hotel> hotels = List.of(hotel);

        Page<Hotel> hotelPage = new PageImpl<>(hotels);
        Pageable pageable = PageRequest.of(0, 10);
        when(hotelRepository.findAll(pageable))
                .thenReturn(hotelPage);
        Page<HotelResponseDTO> responsePage =
                hotelService.getAllHotels(pageable);
        assertEquals(1, responsePage.getTotalElements());

        assertEquals(1L, responsePage.getContent().get(0).getId());
        assertEquals("Taj Hotel", responsePage.getContent().get(0).getName());
        verify(hotelRepository).findAll(pageable);
    }
    @Test
    void testDeleteHotel(){
        Hotel hotel = new Hotel();

        hotel.setId(1L);
        hotel.setName("Taj Hotel");
        hotel.setLocation("Hyderabad");
        hotel.setDescription("Luxury hotel");
        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(hotel));
        hotelService.deleteHotel(1L);
        verify(hotelRepository).delete(hotel);
    }
    @Test
    void testUpdateHotel(){
        Hotel existingHotel = new Hotel();

        existingHotel.setId(1L);
        existingHotel.setName("Old Hotel");
        existingHotel.setLocation("Hyderabad");
        existingHotel.setDescription("Old description");

        HotelRequestDTO requestDTO = new HotelRequestDTO();

        requestDTO.setName("New Hotel");
        requestDTO.setLocation("Chennai");
        requestDTO.setDescription("New description");
        when(hotelRepository.findById(1L))
                .thenReturn(Optional.of(existingHotel));
        when(hotelRepository.save(existingHotel))
                .thenReturn(existingHotel);
        HotelResponseDTO response =
                hotelService.updateHotel(1L, requestDTO);
        assertEquals(1L, response.getId());
        assertEquals("New Hotel", response.getName());
        assertEquals("Chennai", response.getLocation());
        assertEquals("New description", response.getDescription());
        verify(hotelRepository).save(existingHotel);
    }
    
}
