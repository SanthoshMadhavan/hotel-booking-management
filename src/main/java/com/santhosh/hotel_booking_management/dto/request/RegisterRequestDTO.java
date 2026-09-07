package com.santhosh.hotel_booking_management.dto.request;

import com.santhosh.hotel_booking_management.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;


}
