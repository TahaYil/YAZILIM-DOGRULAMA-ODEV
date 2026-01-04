package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.LoginUserDto;
import com.taa.tshirtsatis.dto.RegisterUserDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserDto registerUserDto;
    private LoginUserDto loginUserDto;
    private Users user;

    @BeforeEach
    void setUp() {
        registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("test@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setGender(Gender.MALE);

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password123");

        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);
    }

    @Test
    void signup_ShouldCreateUserSuccessfully() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users result = authenticationService.signup(registerUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertEquals(Gender.MALE, result.getGender());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void signup_ShouldEncodePassword() {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        authenticationService.signup(registerUserDto);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void authenticate_ShouldReturnUserWhenCredentialsAreValid() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Users result = authenticationService.authenticate(loginUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void authenticate_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> authenticationService.authenticate(loginUserDto));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}

