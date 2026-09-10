package com.santhosh.hotel_booking_management.service.impl;

import com.santhosh.hotel_booking_management.dto.request.LoginRequestDTO;
import com.santhosh.hotel_booking_management.dto.request.RegisterRequestDTO;
import com.santhosh.hotel_booking_management.entity.Role;
import com.santhosh.hotel_booking_management.entity.User;
import com.santhosh.hotel_booking_management.exception.AuthenticationException;
import com.santhosh.hotel_booking_management.repository.UserRepository;
import com.santhosh.hotel_booking_management.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }
    @Test
    void testRegisterUser() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO();

        requestDTO.setUsername("santhosh");
        requestDTO.setEmail("santhosh@gmail.com");
        requestDTO.setPassword("password123");
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");
        authService.registerUser(requestDTO);
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("santhosh", savedUser.getUsername());
        assertEquals("santhosh@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.CUSTOMER, savedUser.getRole());
    }
    @Test
    void testLoginUser() {
        LoginRequestDTO requestDTO = new LoginRequestDTO();

        requestDTO.setUsername("santhosh");
        requestDTO.setPassword("password123");
        User user = new User();

        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"
        )).thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");
        String token = authService.loginUser(requestDTO);
        assertEquals("test-jwt-token", token);
        verify(userRepository).findByUsername("santhosh");

        verify(passwordEncoder).matches(
                "password123",
                "encodedPassword"
        );

        verify(jwtService).generateToken(user);

    }
    @Test
    void testLoginUser_InvalidUsername() {

        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("unknown");
        requestDTO.setPassword("password123");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthenticationException.class,
                () -> authService.loginUser(requestDTO)
        );

        verify(userRepository).findByUsername("unknown");
    }
    @Test
    void testLoginUser_WrongPassword() {

        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("santhosh");
        requestDTO.setPassword("wrongPassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("santhosh");
        user.setEmail("santhosh@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("santhosh"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                AuthenticationException.class,
                () -> authService.loginUser(requestDTO)
        );

        verify(userRepository).findByUsername("santhosh");

        verify(passwordEncoder).matches(
                "wrongPassword",
                "encodedPassword"
        );
    }
}
