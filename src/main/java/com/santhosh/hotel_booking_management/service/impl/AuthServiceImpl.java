package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.LoginRequestDTO;
import com.santhosh.hotel_booking_management.dto.request.RegisterRequestDTO;
import com.santhosh.hotel_booking_management.entity.Role;
import com.santhosh.hotel_booking_management.entity.User;
import com.santhosh.hotel_booking_management.exception.AuthenticationException;
import com.santhosh.hotel_booking_management.repository.UserRepository;
import com.santhosh.hotel_booking_management.service.AuthService;
import com.santhosh.hotel_booking_management.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,
                           JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void registerUser(RegisterRequestDTO registerRequestDTO) {
        User user = new User();

        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);
    }

    @Override
    public String loginUser(LoginRequestDTO loginRequestDTO) {
        Optional<User> user = userRepository.findByUsername(loginRequestDTO.getUsername());

        if(user.isEmpty()){
            throw new AuthenticationException("Invalid username or password");
        }
       boolean passwordMatches = passwordEncoder.matches(
               loginRequestDTO.getPassword(),user.get().getPassword()
       );
        if(!passwordMatches){
            throw new AuthenticationException("Invalid username or password");
        }
        return jwtService.generateToken(user.get());
    }
}
