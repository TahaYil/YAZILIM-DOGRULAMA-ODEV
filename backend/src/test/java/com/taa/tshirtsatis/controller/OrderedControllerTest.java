package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.OrderedDto;
import com.taa.tshirtsatis.enums.OrderedState;
import com.taa.tshirtsatis.exception.OrderedNotFoundException;
import com.taa.tshirtsatis.service.JwtService;
import com.taa.tshirtsatis.service.OrderedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderedController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({OrderedControllerTest.TestConfig.class, com.taa.tshirtsatis.exception.GlobalExceptionHandler.class})
class OrderedControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderedService orderedService() {
            return mock(OrderedService.class);
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
    private OrderedService orderedService;

    private OrderedDto orderedDto;
    private List<OrderedDto> orderedList;

    @BeforeEach
    void setUp() {
        orderedDto = new OrderedDto();
        orderedDto.setId(1);
        orderedDto.setOrderId(1);
        orderedDto.setUserId(1);
        orderedDto.setDate(Date.valueOf(LocalDate.now()));
        orderedDto.setState(OrderedState.PENDING);

        OrderedDto orderedDto2 = new OrderedDto();
        orderedDto2.setId(2);
        orderedDto2.setState(OrderedState.SHIPPED);

        orderedList = Arrays.asList(orderedDto, orderedDto2);
    }

    @Test
    @WithMockUser
    void createOrdered_ShouldReturnCreatedOrder() throws Exception {
        // Arrange
        when(orderedService.createOrdered(any(OrderedDto.class))).thenReturn(orderedDto);

        // Act & Assert
        mockMvc.perform(post("/ordered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderedDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("PENDING"));

        verify(orderedService, times(1)).createOrdered(any(OrderedDto.class));
    }

    @Test
    @WithMockUser
    void getOrdered_ShouldReturnOrder_WhenExists() throws Exception {
        // Arrange
        when(orderedService.getOrderedById(1)).thenReturn(orderedDto);

        // Act & Assert
        mockMvc.perform(get("/ordered/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("PENDING"));

        verify(orderedService, times(1)).getOrderedById(1);
    }

    @Test
    @WithMockUser
    void getOrdered_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(orderedService.getOrderedById(999)).thenThrow(new OrderedNotFoundException("Ordered not found"));

        // Act & Assert
        mockMvc.perform(get("/ordered/999"))
                .andExpect(status().isNotFound());

        verify(orderedService, atLeastOnce()).getOrderedById(999);
    }

    @Test
    @WithMockUser
    void getOrderedByUser_ShouldReturnUserOrders() throws Exception {
        // Arrange
        when(orderedService.getOrderedByUserId(1)).thenReturn(orderedList);

        // Act & Assert
        mockMvc.perform(get("/ordered/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderedService, times(1)).getOrderedByUserId(1);
    }

    @Test
    @WithMockUser
    void getAllOrdered_ShouldReturnAllOrders() throws Exception {
        // Arrange
        when(orderedService.getAllOrdered()).thenReturn(orderedList);

        // Act & Assert
        mockMvc.perform(get("/ordered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderedService, times(1)).getAllOrdered();
    }

    @Test
    @WithMockUser
    void updateOrdered_ShouldReturnUpdatedOrder() throws Exception {
        // Arrange
        when(orderedService.updateOrdered(eq(1), any(OrderedDto.class))).thenReturn(orderedDto);

        // Act & Assert
        mockMvc.perform(put("/ordered/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderedService, times(1)).updateOrdered(eq(1), any(OrderedDto.class));
    }

    @Test
    @WithMockUser
    void deleteOrdered_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(orderedService).deleteOrdered(1);

        // Act & Assert
        mockMvc.perform(delete("/ordered/1"))
                .andExpect(status().isNoContent());

        verify(orderedService, times(1)).deleteOrdered(1);
    }

    @Test
    @WithMockUser
    void updateOrderedState_ShouldReturnUpdatedOrder() throws Exception {
        // Arrange
        OrderedDto updatedDto = new OrderedDto();
        updatedDto.setId(1);
        updatedDto.setState(OrderedState.PROCESSING);

        when(orderedService.updateOrderState(1, OrderedState.PROCESSING)).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/ordered/1/state")
                        .param("state", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PROCESSING"));

        verify(orderedService, times(1)).updateOrderState(1, OrderedState.PROCESSING);
    }
//OLMUYOR ÇÜNKÜ 2 FARKLI CONTROLLER VAR BUNLAR AYRILMALI
//    @Test
//    @WithMockUser
//    void getOrderedByState_ShouldReturnFilteredOrders() throws Exception {
//        // Arrange
//        when(orderedService.getOrderedByState(any(OrderedState.class))).thenReturn(Arrays.asList(orderedDto));
//
//        // Act & Assert
//        mockMvc.perform(get("/state/PENDING")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].state").value("PENDING"));
//
//        //verify(orderedService, atLeastOnce()).getOrderedByState(OrderedState.PENDING);
//    }

    @Test
    @WithMockUser
    void getOrderedState_ShouldReturnOrderState() throws Exception {
        // Arrange
        when(orderedService.getOrderedById(1)).thenReturn(orderedDto);

        // Act & Assert
        mockMvc.perform(get("/ordered/state/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("\"PENDING\""));

        verify(orderedService, atLeastOnce()).getOrderedById(1);
    }

    @Test
    @WithMockUser
    void getOrderedByDate_ShouldReturnOrdersForDate() throws Exception {
        // Arrange
        Date testDate = Date.valueOf(LocalDate.now());
        when(orderedService.getOrderedByDate(any(Date.class))).thenReturn(orderedList);

        // Act & Assert
        mockMvc.perform(get("/ordered/date/" + testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderedService, times(1)).getOrderedByDate(any(Date.class));
    }

    @Test
    @WithMockUser
    void getTodaysOrders_ShouldReturnTodaysOrders() throws Exception {
        // Arrange
        when(orderedService.getTodaysOrders()).thenReturn(orderedList);

        // Act & Assert
        mockMvc.perform(get("/ordered/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(orderedService, times(1)).getTodaysOrders();
    }

    @Test
    @WithMockUser
    void countOrdersByState_ShouldReturnCount() throws Exception {
        // Arrange
        when(orderedService.countOrdersByState(OrderedState.PENDING)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/ordered/count/PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(orderedService, times(1)).countOrdersByState(OrderedState.PENDING);
    }

    @Test
    @WithMockUser
    void isDelivered_ShouldReturnTrue_WhenDelivered() throws Exception {
        // Arrange
        when(orderedService.isDelivered(1)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/ordered/1/delivered"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(orderedService, atLeastOnce()).isDelivered(1);
    }

    @Test
    @WithMockUser
    void isDelivered_ShouldReturnFalse_WhenNotDelivered() throws Exception {
        // Arrange
        when(orderedService.isDelivered(1)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/ordered/1/delivered"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(orderedService, atLeastOnce()).isDelivered(1);
    }
}
