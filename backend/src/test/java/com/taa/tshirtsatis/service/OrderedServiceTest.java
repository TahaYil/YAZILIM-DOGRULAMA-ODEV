package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.OrderedDto;
import com.taa.tshirtsatis.entity.Order;
import com.taa.tshirtsatis.entity.Ordered;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.OrderedState;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.OrderedNotFoundException;
import com.taa.tshirtsatis.exception.OrderNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.OrderRepository;
import com.taa.tshirtsatis.repository.OrderedRepository;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderedServiceTest {

    @Mock
    private OrderedRepository orderedRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private OrderedService orderedService;

    private Ordered ordered;
    private OrderedDto orderedDto;
    private Order order;
    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setGender(Gender.MALE);

        order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setTotalPrice(99.99f);

        ordered = new Ordered();
        ordered.setId(1);
        ordered.setOrder(order);
        ordered.setUser(user);
        ordered.setDate(Date.valueOf(LocalDate.now()));
        ordered.setState(OrderedState.PENDING);

        orderedDto = new OrderedDto();
        orderedDto.setId(1);
        orderedDto.setOrderId(1);
        orderedDto.setUserId(1);
        orderedDto.setDate(Date.valueOf(LocalDate.now()));
        orderedDto.setState(OrderedState.PENDING);
    }

    @Test
    void getAllOrdered_ShouldReturnAllOrdered() {
        // Arrange
        when(orderedRepository.findAll()).thenReturn(Arrays.asList(ordered));

        // Act
        List<OrderedDto> result = orderedService.getAllOrdered();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderedRepository, times(1)).findAll();
    }

    @Test
    void getOrderedById_ShouldReturnOrdered_WhenExists() {
        // Arrange
        when(orderedRepository.findById(1)).thenReturn(Optional.of(ordered));

        // Act
        OrderedDto result = orderedService.getOrderedById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(OrderedState.PENDING, result.getState());
        verify(orderedRepository, times(1)).findById(1);
    }

    @Test
    void getOrderedById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderedRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderedNotFoundException.class, () -> orderedService.getOrderedById(999));
        verify(orderedRepository, times(1)).findById(999);
    }

    @Test
    void createOrdered_ShouldCreateSuccessfully() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderedRepository.save(any(Ordered.class))).thenReturn(ordered);

        // Act
        OrderedDto result = orderedService.createOrdered(orderedDto);

        // Assert
        assertNotNull(result);
        assertEquals(OrderedState.PENDING, result.getState());
        verify(orderRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
        verify(orderedRepository, times(1)).save(any(Ordered.class));
    }

    @Test
    void createOrdered_ShouldThrowException_WhenOrderedDtoIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderedService.createOrdered(null));
        verify(orderedRepository, never()).save(any(Ordered.class));
    }

    @Test
    void createOrdered_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderedService.createOrdered(orderedDto));
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void createOrdered_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> orderedService.createOrdered(orderedDto));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void updateOrdered_ShouldUpdateSuccessfully_WhenExists() {
        // Arrange
        OrderedDto updateDto = new OrderedDto();
        updateDto.setOrderId(1);
        updateDto.setUserId(1);
        updateDto.setDate(Date.valueOf(LocalDate.now()));
        updateDto.setState(OrderedState.SHIPPED);

        when(orderedRepository.findById(1)).thenReturn(Optional.of(ordered));
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(orderedRepository.save(any(Ordered.class))).thenReturn(ordered);

        // Act
        OrderedDto result = orderedService.updateOrdered(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(orderedRepository, times(1)).findById(1);
        verify(orderedRepository, times(1)).save(any(Ordered.class));
    }

    @Test
    void updateOrdered_ShouldThrowException_WhenOrderedDtoIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderedService.updateOrdered(1, null));
        verify(orderedRepository, never()).save(any(Ordered.class));
    }

    @Test
    void updateOrdered_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderedRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderedNotFoundException.class,
                () -> orderedService.updateOrdered(999, orderedDto));
        verify(orderedRepository, times(1)).findById(999);
    }

    @Test
    void deleteOrdered_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(orderedRepository.existsById(1)).thenReturn(true);

        // Act
        orderedService.deleteOrdered(1);

        // Assert
        verify(orderedRepository, times(1)).existsById(1);
        verify(orderedRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteOrdered_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderedRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(OrderedNotFoundException.class, () -> orderedService.deleteOrdered(999));
        verify(orderedRepository, never()).deleteById(anyInt());
    }

    @Test
    void getOrderedByUserId_ShouldReturnUserOrdered() {
        // Arrange
        when(userRepository.existsById(1)).thenReturn(true);
        when(orderedRepository.findByUserId(1)).thenReturn(Arrays.asList(ordered));

        // Act
        List<OrderedDto> result = orderedService.getOrderedByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).existsById(1);
        verify(orderedRepository, times(1)).findByUserId(1);
    }

    @Test
    void getOrderedByUserId_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> orderedService.getOrderedByUserId(999));
        verify(userRepository, times(1)).existsById(999);
    }
}

