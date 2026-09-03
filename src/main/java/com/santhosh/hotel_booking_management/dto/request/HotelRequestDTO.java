package com.santhosh.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRequestDTO {
       @NotBlank(message = "Name cannot be blank")
       private String name;

       @NotBlank(message = "Location cannot be blank")
       private String location;

       private String description;
}
