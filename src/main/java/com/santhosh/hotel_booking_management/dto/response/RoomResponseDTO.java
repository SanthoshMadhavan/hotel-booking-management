package com.santhosh.hotel_booking_management.dto.response;

import com.santhosh.hotel_booking_management.entity.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomResponseDTO {
    private Long id;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal price;
    private Boolean available;
    private Long hotelId;
}
