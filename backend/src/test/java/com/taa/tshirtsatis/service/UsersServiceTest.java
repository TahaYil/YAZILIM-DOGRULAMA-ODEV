package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.UsersDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    private Users user;
    private UsersDto usersDto;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        usersDto = new UsersDto();
        usersDto.setId(1);
        usersDto.setEmail("test@example.com");
        usersDto.setPassword("password123");
        usersDto.setGender(Gender.MALE);
        usersDto.setRole(Role.USER);
    }

    @Test
    void processOAuthPostLogin_ShouldReturnExistingUser_WhenUserExists() {
        // Arrange
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Users result = usersService.processOAuthPostLogin("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(usersRepository, times(1)).findByEmail("test@example.com");
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void processOAuthPostLogin_ShouldCreateNewUser_WhenUserDoesNotExist() {
        // Arrange
        when(usersRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users result = usersService.processOAuthPostLogin("newuser@example.com");

        // Assert
        assertNotNull(result);
        verify(usersRepository, times(1)).findByEmail("newuser@example.com");
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(usersRepository.findAll()).thenReturn(Arrays.asList(user));

        // Act
        List<UsersDto> result = usersService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(usersRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        UsersDto result = usersService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(usersRepository, times(1)).findById(1);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> usersService.getUserById(999));
        verify(usersRepository, times(1)).findById(999);
    }

    @Test
    void createUser_ShouldCreateSuccessfully() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        UsersDto result = usersService.createUser(usersDto);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenPasswordIsNull() {
        // Arrange
        usersDto.setPassword(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> usersService.createUser(usersDto));
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        usersDto.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> usersService.createUser(usersDto));
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully_WhenExists() {
        // Arrange
        UsersDto updateDto = new UsersDto();
        updateDto.setEmail("updated@example.com");
        updateDto.setPassword("newpassword");
        updateDto.setGender(Gender.FEMALE);
        updateDto.setRole(Role.ADMIN);

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        UsersDto result = usersService.updateUser(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(usersRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordIsNull() {
        // Arrange
        UsersDto updateDto = new UsersDto();
        updateDto.setEmail("updated@example.com");
        updateDto.setPassword(null);
        updateDto.setGender(Gender.FEMALE);
        updateDto.setRole(Role.ADMIN);

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        UsersDto result = usersService.updateUser(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordIsEmpty() {
        // Arrange
        UsersDto updateDto = new UsersDto();
        updateDto.setEmail("updated@example.com");
        updateDto.setPassword("");
        updateDto.setGender(Gender.FEMALE);
        updateDto.setRole(Role.ADMIN);

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        // Act
        UsersDto result = usersService.updateUser(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(usersRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> usersService.updateUser(999, usersDto));
        verify(usersRepository, times(1)).findById(999);
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void deleteUser_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(usersRepository.existsById(1)).thenReturn(true);

        // Act
        usersService.deleteUser(1);

        // Assert
        verify(usersRepository, times(1)).existsById(1);
        verify(usersRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(usersRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> usersService.deleteUser(999));
        verify(usersRepository, times(1)).existsById(999);
        verify(usersRepository, never()).deleteById(anyInt());
    }
}

