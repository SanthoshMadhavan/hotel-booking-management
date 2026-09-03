package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.RoomRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.RoomResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.entity.Room;
import com.santhosh.hotel_booking_management.entity.RoomType;
import com.santhosh.hotel_booking_management.exception.ResourceNotFoundException;
import com.santhosh.hotel_booking_management.repository.HotelRepository;
import com.santhosh.hotel_booking_management.repository.RoomRepository;
import com.santhosh.hotel_booking_management.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() ->new ResourceNotFoundException(
                        "Hotel not found with id: " + roomRequestDTO.getHotelId()
                ));
        Room room = new Room();

        room.setRoomNumber(roomRequestDTO.getRoomNumber());
        room.setRoomType(roomRequestDTO.getRoomType());
        room.setPrice(roomRequestDTO.getPrice());
        room.setAvailable(roomRequestDTO.getAvailable());
        room.setHotel(hotel);


        Room savedRoom = roomRepository.save(room);
        return mapToResponseDTO(savedRoom);
    }

    @Override
    public List<RoomResponseDTO> getAllRooms() {
       List<Room> rooms = roomRepository.findAll();
       List<RoomResponseDTO> responseList = new ArrayList<>();

       for(Room room : rooms){
           responseList.add(mapToResponseDTO(room));
       }
       return responseList;
    }

    @Override
    public RoomResponseDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id
                        ));

        return mapToResponseDTO(room);
    }

    @Override
    public RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id
                        ));

        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hotel not found with id: " +
                                        roomRequestDTO.getHotelId()
                        ));

        existingRoom.setRoomNumber(roomRequestDTO.getRoomNumber());
        existingRoom.setRoomType(roomRequestDTO.getRoomType());
        existingRoom.setPrice(roomRequestDTO.getPrice());
        existingRoom.setAvailable(roomRequestDTO.getAvailable());
        existingRoom.setHotel(hotel);

        Room updatedRoom = roomRepository.save(existingRoom);

        return mapToResponseDTO(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id
                        ));

        roomRepository.delete(existingRoom);
    }

    @Override
    public List<RoomResponseDTO> filterRoomsByType(RoomType roomType) {
        List<Room> rooms = roomRepository.findByRoomType(roomType);
        List<RoomResponseDTO> responseList = new ArrayList<>();

        for(Room room : rooms){
            responseList.add(mapToResponseDTO(room));
        }
        return responseList;
    }

    @Override
    public List<RoomResponseDTO> filterRoomsByMaxPrice(BigDecimal price) {
         List<Room> rooms =  roomRepository.findByPriceLessThanEqual(price);
         List<RoomResponseDTO> responseList = new ArrayList<>();

         for(Room room : rooms){
             responseList.add(mapToResponseDTO(room));
         }
         return responseList;
    }

    @Override
    public List<RoomResponseDTO> filterRoomsByAvailability(Boolean available) {
         List<Room> rooms = roomRepository.findByAvailable(available);
         List<RoomResponseDTO> responseList = new ArrayList<>();

         for(Room room : rooms){
             responseList.add(mapToResponseDTO(room));
         }
         return responseList;
    }

    @Override
    public Page<RoomResponseDTO> filterRooms(RoomType roomType, Boolean available,
                                             BigDecimal price, Pageable pageable) {
        Page<Room> rooms = roomRepository.findByRoomTypeAndAvailableAndPriceLessThanEqual(roomType,
                available,price,pageable);
        return rooms.map(this::mapToResponseDTO);
    }

    private RoomResponseDTO mapToResponseDTO(Room room) {

        RoomResponseDTO responseDTO = new RoomResponseDTO();

        responseDTO.setId(room.getId());
        responseDTO.setRoomNumber(room.getRoomNumber());
        responseDTO.setRoomType(room.getRoomType());
        responseDTO.setPrice(room.getPrice());
        responseDTO.setAvailable(room.getAvailable());
        responseDTO.setHotelId(room.getHotel().getId());

        return responseDTO;
    }
}
