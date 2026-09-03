package com.santhosh.hotel_booking_management.controller;

import com.santhosh.hotel_booking_management.dto.request.RoomRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.RoomResponseDTO;
import com.santhosh.hotel_booking_management.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(
        @Valid @RequestBody RoomRequestDTO roomRequestDTO
            ){
        RoomResponseDTO response = roomService.createRoom(roomRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {

        List<RoomResponseDTO> response =
                roomService.getAllRooms();

        return ResponseEntity.ok(response);
    }
    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                roomService.getRoomById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Long id,
         @Valid   @RequestBody RoomRequestDTO roomRequestDTO) {

        RoomResponseDTO response =
                roomService.updateRoom(id, roomRequestDTO);

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return ResponseEntity.noContent().build();
    }
}
