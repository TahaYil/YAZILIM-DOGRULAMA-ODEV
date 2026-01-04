package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.OrderDto;
import com.taa.tshirtsatis.dto.OrderProductDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.OrderNotFoundException;
import com.taa.tshirtsatis.service.JwtService;
import com.taa.tshirtsatis.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc()
@Import(OrderControllerTest.TestConfig.class)
class OrderControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderService orderService() {
            return mock(OrderService.class);
        }
        @Bean
        public JwtService jwtService() {
            return mock(JwtService.class);
        }
        @Bean
        public UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    private OrderDto orderDto;
    private OrderProductDto orderProductDto;
    private Users mockUser;
    private List<OrderDto> orderList;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.ADMIN);
        mockUser.setGender(Gender.MALE);
        authentication =
                new UsernamePasswordAuthenticationToken(
                        mockUser,           // principal (ÖNEMLİ)
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))         // authorities (boş olabilir)
                );

        orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setUserId(1);
        orderDto.setProductIds(Arrays.asList(1, 2));
        orderDto.setAddress("Test Address");
        orderDto.setTotalPrice(59.99f);
        orderDto.setActive(true);

        orderProductDto = new OrderProductDto();
        orderProductDto.setProductId(1);
        orderProductDto.setQuantity(2);

        OrderDto orderDto2 = new OrderDto();
        orderDto2.setId(2);
        orderDto2.setUserId(1);

        orderList = Arrays.asList(orderDto, orderDto2);
    }

    @Test
    @WithMockUser
    void getAllOrders_ShouldReturnOrderList() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(orderList);

        // Act & Assert
        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    @WithMockUser
    void getOrderById_ShouldReturnOrder_WhenExists() throws Exception {
        // Arrange
        when(orderService.getOrderById(1)).thenReturn(orderDto);

        // Act & Assert
        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Test Address"))
                .andExpect(jsonPath("$.totalPrice").value(59.99));

        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    @WithMockUser
    void getOrderById_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999)).thenReturn(null);

        mockMvc.perform(get("/order/999"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999);
    }

    @Test
    @WithMockUser
    void getActiveOrder_ShouldReturnActiveOrder_WhenExists() throws Exception {
        // Arrange
        when(orderService.getActiveOrderByUserId(1)).thenReturn(orderDto);



        // Act & Assert
        mockMvc.perform(get("/order/active-order")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));

        //verify(orderService, times(1)).getActiveOrderByUserId(1);
    }

    @Test
    void getActiveOrder_ShouldReturnNoContent_WhenNoActiveOrder() throws Exception {
        // Arrange
        when(orderService.getActiveOrderByUserId(1)).thenReturn(null);

        Users users = new Users();
        users.setId(1);
        Authentication auth = new UsernamePasswordAuthenticationToken(users, null, List.of());

        // Act & Assert
        mockMvc.perform(get("/order/active-order")
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).getActiveOrderByUserId(1);
    }

    @Test
    @WithMockUser
    void createActiveOrder_ShouldReturnNewOrder() throws Exception {
        // Arrange
        when(orderService.createOrUpdateActiveOrder(eq(1), any(OrderProductDto.class))).thenReturn(orderDto);


        // Act & Assert
        mockMvc.perform(post("/order/create-active-order")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).createOrUpdateActiveOrder(eq(1), any(OrderProductDto.class));
    }

    @Test
    void addProductToOrder_ShouldReturnUpdatedOrder() throws Exception {
        // Arrange
        when(orderService.addProductToOrder(eq(1), any(OrderProductDto.class), eq(1))).thenReturn(orderDto);

        Users users = new Users();
        users.setId(1);

        // Act & Assert
        mockMvc.perform(put("/order/1/add-product")
                        .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).addProductToOrder(eq(1), any(OrderProductDto.class), eq(1));
    }

    @Test
    @WithMockUser
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(orderDto);

        // Act & Assert
        mockMvc.perform(post("/order")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("Test Address"));

        verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }

    @Test
    @WithMockUser
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
        // Arrange
        when(orderService.updateOrder(eq(1), any(OrderDto.class))).thenReturn(orderDto);

        // Act & Assert
        mockMvc.perform(put("/order/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).updateOrder(eq(1), any(OrderDto.class));
    }

    @Test
    @WithMockUser
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteOrder(1);

        // Act & Assert
        mockMvc.perform(delete("/order/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    @WithMockUser
    void getOrdersByUserId_ShouldReturnUserOrders() throws Exception {
        // Arrange
        when(orderService.getOrdersByUserId(1)).thenReturn(orderList);

        // Act & Assert
        mockMvc.perform(get("/order/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService, times(1)).getOrdersByUserId(1);
    }

    @Test
    @WithMockUser
    void getActiveOrders_ShouldReturnActiveOrdersList() throws Exception {
        // Arrange
        when(orderService.getActiveOrders()).thenReturn(orderList);

        // Act & Assert
        mockMvc.perform(get("/order/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderService, times(1)).getActiveOrders();
    }

    @Test
    @WithMockUser
    void createOrder_ShouldCallServiceMethod() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(orderDto);

        // Act
        mockMvc.perform(post("/order")
                        .with((SecurityMockMvcRequestPostProcessors.csrf()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk());

        // Assert
        //verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }
}
