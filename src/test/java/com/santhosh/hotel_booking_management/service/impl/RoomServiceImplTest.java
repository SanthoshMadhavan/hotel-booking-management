package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.RoomRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.RoomResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.entity.Room;
import com.santhosh.hotel_booking_management.entity.RoomType;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.HotelRepository;
import com.santhosh.hotel_booking_management.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp(){
        roomService = new RoomServiceImpl(roomRepository,hotelRepository);
    }

    @Test
    void testCreateRoom(){
        RoomRequestDTO requestDTO = new RoomRequestDTO();
        requestDTO.setRoomNumber("101");
        requestDTO.setRoomType(RoomType.DELUXE);
        requestDTO.setPrice(new BigDecimal("4000.00"));
        requestDTO.setAvailable(true);
        requestDTO.setHotelId(1L);

        Hotel hotel = new Hotel();

        hotel.setId(1L);
        hotel.setName("Taj Hotel");
        hotel.setLocation("Hyderabad");
        hotel.setDescription("Luxury hotel");

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        Room savedRoom = new Room();

        savedRoom.setId(1L);
        savedRoom.setRoomNumber("101");
        savedRoom.setRoomType(RoomType.DELUXE);
        savedRoom.setPrice(new BigDecimal("4000.00"));
        savedRoom.setAvailable(true);
        savedRoom.setHotel(hotel);

        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        RoomResponseDTO response = roomService.createRoom(requestDTO);

        assertEquals(1L, response.getId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(RoomType.DELUXE, response.getRoomType());
        assertEquals(new BigDecimal("4000.00"), response.getPrice());
        assertEquals(true, response.getAvailable());
        assertEquals(1L, response.getHotelId());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void testGetRoomById(){
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        RoomResponseDTO response = roomService.getRoomById(1L);
        assertEquals(1L, response.getId());
        assertEquals("101", response.getRoomNumber());
        assertEquals(RoomType.DELUXE, response.getRoomType());
        assertEquals(new BigDecimal("4000.00"), response.getPrice());
        assertEquals(true, response.getAvailable());
        assertEquals(1L, response.getHotelId());
    }
    @Test
    void testGetRoomById_NotFound(){
        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roomService.getRoomById(999L)
        );
    }
    @Test
    void testGetAllRooms(){
        Room room1 = new Room();

        room1.setId(1L);
        room1.setRoomNumber("101");
        room1.setRoomType(RoomType.DELUXE);
        room1.setPrice(new BigDecimal("4000.00"));
        room1.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room1.setHotel(hotel);


        Room room2 = new Room();

        room2.setId(2L);
        room2.setRoomNumber("102");
        room2.setRoomType(RoomType.SUITE);
        room2.setPrice(new BigDecimal("6000.00"));
        room2.setAvailable(true);

        room2.setHotel(hotel);
        List<Room> rooms = List.of(room1, room2);

        when(roomRepository.findAll())
                .thenReturn(rooms);
        List<RoomResponseDTO> responseList =
                roomService.getAllRooms();
        assertEquals(2, responseList.size());

        assertEquals(1L, responseList.get(0).getId());
        assertEquals("101", responseList.get(0).getRoomNumber());
        assertEquals(RoomType.DELUXE, responseList.get(0).getRoomType());
        assertEquals(1L, responseList.get(0).getHotelId());

        assertEquals(2L, responseList.get(1).getId());
        assertEquals("102", responseList.get(1).getRoomNumber());
        assertEquals(RoomType.SUITE, responseList.get(1).getRoomType());
        assertEquals(1L, responseList.get(1).getHotelId());
        verify(roomRepository).findAll();
    }
    @Test
    void testFilterRoomsByType(){
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);
        List<Room> rooms = List.of(room);

        when(roomRepository.findByRoomType(RoomType.DELUXE))
                .thenReturn(rooms);
        List<RoomResponseDTO> responseList =
                roomService.filterRoomsByType(RoomType.DELUXE);
        assertEquals(1, responseList.size());

        assertEquals(1L, responseList.get(0).getId());
        assertEquals("101", responseList.get(0).getRoomNumber());
        assertEquals(RoomType.DELUXE, responseList.get(0).getRoomType());
        assertEquals(new BigDecimal("4000.00"), responseList.get(0).getPrice());
        assertEquals(true, responseList.get(0).getAvailable());
        assertEquals(1L, responseList.get(0).getHotelId());

        verify(roomRepository).findByRoomType(RoomType.DELUXE);
    }
    @Test
    void testFilterRoomsByMaxPrice(){
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);
        List<Room> rooms = List.of(room);

        when(roomRepository.findByPriceLessThanEqual(new BigDecimal("5000.00")))
                .thenReturn(rooms);
        List<RoomResponseDTO> responseList =
                roomService.filterRoomsByMaxPrice(new BigDecimal("5000.00"));
        assertEquals(1, responseList.size());

        assertEquals(1L, responseList.get(0).getId());
        assertEquals("101", responseList.get(0).getRoomNumber());
        assertEquals(RoomType.DELUXE, responseList.get(0).getRoomType());
        assertEquals(new BigDecimal("4000.00"), responseList.get(0).getPrice());
        assertEquals(true, responseList.get(0).getAvailable());
        assertEquals(1L, responseList.get(0).getHotelId());

        verify(roomRepository)
                .findByPriceLessThanEqual(new BigDecimal("5000.00"));
    }
    @Test
    void testFilterRoomsByAvailability() {
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);
        List<Room> rooms = List.of(room);

        when(roomRepository.findByAvailable(true))
                .thenReturn(rooms);
        List<RoomResponseDTO> responseList =
                roomService.filterRoomsByAvailability(true);
        assertEquals(1, responseList.size());

        assertEquals(1L, responseList.get(0).getId());
        assertEquals("101", responseList.get(0).getRoomNumber());
        assertEquals(RoomType.DELUXE, responseList.get(0).getRoomType());
        assertEquals(new BigDecimal("4000.00"), responseList.get(0).getPrice());
        assertEquals(true, responseList.get(0).getAvailable());
        assertEquals(1L, responseList.get(0).getHotelId());

        verify(roomRepository).findByAvailable(true);
    }
    @Test
    void testFilterRooms() {
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);
        List<Room> rooms = List.of(room);

        Page<Room> roomPage = new PageImpl<>(rooms);
        Pageable pageable = PageRequest.of(0, 10);
        when(roomRepository.findByRoomTypeAndAvailableAndPriceLessThanEqual(
                RoomType.DELUXE,
                true,
                new BigDecimal("5000.00"),
                pageable
        )).thenReturn(roomPage);
        Page<RoomResponseDTO> responsePage =
                roomService.filterRooms(
                        RoomType.DELUXE,
                        true,
                        new BigDecimal("5000.00"),
                        pageable
                );
        assertEquals(1, responsePage.getTotalElements());

        assertEquals(1, responsePage.getContent().size());

        assertEquals(1L, responsePage.getContent().get(0).getId());
        assertEquals("101", responsePage.getContent().get(0).getRoomNumber());
        assertEquals(RoomType.DELUXE, responsePage.getContent().get(0).getRoomType());
        assertEquals(new BigDecimal("4000.00"),
                responsePage.getContent().get(0).getPrice());
        assertEquals(true, responsePage.getContent().get(0).getAvailable());
        assertEquals(1L, responsePage.getContent().get(0).getHotelId());
        verify(roomRepository).findByRoomTypeAndAvailableAndPriceLessThanEqual(
                RoomType.DELUXE,
                true,
                new BigDecimal("5000.00"),
                pageable
        );
    }
    @Test
    void testUpdateRoom() {
        Room existingRoom = new Room();

        existingRoom.setId(1L);
        existingRoom.setRoomNumber("101");
        existingRoom.setRoomType(RoomType.DELUXE);
        existingRoom.setPrice(new BigDecimal("4000.00"));
        existingRoom.setAvailable(true);

        Hotel oldHotel = new Hotel();
        oldHotel.setId(1L);

        existingRoom.setHotel(oldHotel);
        RoomRequestDTO requestDTO = new RoomRequestDTO();

        requestDTO.setRoomNumber("201");
        requestDTO.setRoomType(RoomType.SUITE);
        requestDTO.setPrice(new BigDecimal("6000.00"));
        requestDTO.setAvailable(false);
        requestDTO.setHotelId(2L);
        Hotel newHotel = new Hotel();
        newHotel.setId(2L);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(existingRoom));

        when(hotelRepository.findById(2L))
                .thenReturn(Optional.of(newHotel));
        when(roomRepository.save(existingRoom))
                .thenReturn(existingRoom);
        RoomResponseDTO response =
                roomService.updateRoom(1L, requestDTO);
        assertEquals(1L, response.getId());
        assertEquals("201", response.getRoomNumber());
        assertEquals(RoomType.SUITE, response.getRoomType());
        assertEquals(new BigDecimal("6000.00"), response.getPrice());
        assertEquals(false, response.getAvailable());
        assertEquals(2L, response.getHotelId());
        verify(roomRepository).save(existingRoom);
    }
    @Test
    void testDeleteRoom() {
        Room room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(new BigDecimal("4000.00"));
        room.setAvailable(true);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        room.setHotel(hotel);
        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));
        roomService.deleteRoom(1L);
        verify(roomRepository).delete(room);
    }
}
