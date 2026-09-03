package com.santhosh.hotel_booking_management.dto.request;

import com.santhosh.hotel_booking_management.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomRequestDTO {
    @NotBlank(message = "Room number cannot be blank")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0",inclusive = false,
            message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Availability is required")
    private Boolean available;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;
}
