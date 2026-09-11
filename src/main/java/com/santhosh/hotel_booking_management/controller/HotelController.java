package com.santhosh.hotel_booking_management.controller;

import com.santhosh.hotel_booking_management.dto.request.HotelRequestDTO;
import com.santhosh.hotel_booking_management.dto.response.HotelResponseDTO;
import com.santhosh.hotel_booking_management.entity.Hotel;
import com.santhosh.hotel_booking_management.service.HotelService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }
    @PostMapping
    public ResponseEntity<HotelResponseDTO> createHotel(
           @Valid @RequestBody HotelRequestDTO hotelRequestDTO) {

        HotelResponseDTO response = hotelService.createHotel(hotelRequestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<HotelResponseDTO>> getAllHotels(
            @ParameterObject Pageable pageable) {

        Page<HotelResponseDTO> response = hotelService.getAllHotels(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/location")
    public ResponseEntity<List<HotelResponseDTO>> searchHotelsByLocation(@RequestParam String location){
         List<HotelResponseDTO> response = hotelService.searchHotelsByLocation(location);
         return ResponseEntity.ok(response);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<HotelResponseDTO>> searchHotelsByName(@RequestParam String name){
        List<HotelResponseDTO> response = hotelService.searchHotelsByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> updateHotel(
            @PathVariable Long id,
            @RequestBody HotelRequestDTO hotelRequestDTO) {

        HotelResponseDTO response =
                hotelService.updateHotel(id, hotelRequestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(
            @PathVariable Long id) {

        hotelService.deleteHotel(id);

        return ResponseEntity.noContent().build();
    }




}
