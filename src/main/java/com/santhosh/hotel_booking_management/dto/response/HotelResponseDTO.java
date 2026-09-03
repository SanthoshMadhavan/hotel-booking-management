package com.santhosh.hotel_booking_management.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelResponseDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
}
