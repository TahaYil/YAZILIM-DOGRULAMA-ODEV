package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.OrderDto;
import com.taa.tshirtsatis.dto.OrderProductDto;
import com.taa.tshirtsatis.entity.Order;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.OrderNotFoundException;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.OrderRepository;
import com.taa.tshirtsatis.repository.ProductRepository;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDto orderDto;
    private Users user;
    private Product product;
    private OrderProductDto orderProductDto;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setGender(Gender.MALE);

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(29.99f);

        order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setProducts(new HashSet<>(Arrays.asList(product)));
        order.setAddress("Test Address");
        order.setTotalPrice(29.99f);
        order.setActive(true);

        orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setUserId(1);
        orderDto.setProductIds(Arrays.asList(1));
        orderDto.setAddress("Test Address");
        orderDto.setActive(true);

        orderProductDto = new OrderProductDto();
        orderProductDto.setProductId(1);
        orderProductDto.setQuantity(2);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        // Act
        List<OrderDto> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // Act
        OrderDto result = orderService.getOrderById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void getOrderById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999));
        verify(orderRepository, times(1)).findById(999);
    }

    @Test
    void getActiveOrderByUserId_ShouldReturnActiveOrder_WhenExists() {
        // Arrange
        when(orderRepository.findByUser_IdAndActiveTrue(1)).thenReturn(Optional.of(order));

        // Act
        OrderDto result = orderService.getActiveOrderByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderRepository, times(1)).findByUser_IdAndActiveTrue(1);
    }

    @Test
    void getActiveOrderByUserId_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(orderRepository.findByUser_IdAndActiveTrue(1)).thenReturn(Optional.empty());

        // Act
        OrderDto result = orderService.getActiveOrderByUserId(1);

        // Assert
        assertNull(result);
        verify(orderRepository, times(1)).findByUser_IdAndActiveTrue(1);
    }

    @Test
    void createOrUpdateActiveOrder_ShouldCreateNewOrder() {
        // Arrange
        when(orderRepository.findAllByUser_IdAndActiveTrue(1)).thenReturn(new ArrayList<>());
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDto result = orderService.createOrUpdateActiveOrder(1, orderProductDto);

        // Assert
        assertNotNull(result);
        verify(usersRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrUpdateActiveOrder_ShouldThrowException_WhenOrderProductDtoIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrUpdateActiveOrder(1, null));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrUpdateActiveOrder_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(orderRepository.findAllByUser_IdAndActiveTrue(1)).thenReturn(new ArrayList<>());
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> orderService.createOrUpdateActiveOrder(1, orderProductDto));
    }

    @Test
    void createOrUpdateActiveOrder_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(orderRepository.findAllByUser_IdAndActiveTrue(1)).thenReturn(new ArrayList<>());
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> orderService.createOrUpdateActiveOrder(1, orderProductDto));
    }

    @Test
    void addProductToOrder_ShouldAddProductSuccessfully() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDto result = orderService.addProductToOrder(1, orderProductDto, 1);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void addProductToOrder_ShouldThrowException_WhenOrderProductDtoIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.addProductToOrder(1, null, 1));
    }

    @Test
    void createOrder_ShouldCreateSuccessfully() {
        // Arrange
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDto result = orderService.createOrder(orderDto);

        // Assert
        assertNotNull(result);
        verify(usersRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldUpdateSuccessfully_WhenExists() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderDto result = orderService.updateOrder(1, orderDto);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrder(999, orderDto));
    }

    @Test
    void deleteOrder_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(orderRepository.existsById(1)).thenReturn(true);

        // Act
        orderService.deleteOrder(1);

        // Assert
        verify(orderRepository, times(1)).existsById(1);
        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteOrder_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(orderRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(999));
        verify(orderRepository, never()).deleteById(anyInt());
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Arrange
        when(orderRepository.findByUserId(1)).thenReturn(Arrays.asList(order));

        // Act
        List<OrderDto> result = orderService.getOrdersByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUserId(1);
    }

    @Test
    void getActiveOrders_ShouldReturnActiveOrders() {
        // Arrange
        when(orderRepository.findByActiveTrue()).thenReturn(Arrays.asList(order));

        // Act
        List<OrderDto> result = orderService.getActiveOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByActiveTrue();
    }
}
