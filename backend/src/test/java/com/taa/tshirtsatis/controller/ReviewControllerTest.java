package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.ReviewDto;
import com.taa.tshirtsatis.exception.ReviewNotFoundException;
import com.taa.tshirtsatis.service.JwtService;
import com.taa.tshirtsatis.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ReviewControllerTest.TestConfig.class)
class ReviewControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ReviewService reviewService() {
            return mock(ReviewService.class);
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
    private ReviewService reviewService;

    private ReviewDto reviewDto;
    private List<ReviewDto> reviewList;

    @BeforeEach
    void setUp() {
        reviewDto = new ReviewDto();
        reviewDto.setId(1);
        reviewDto.setComment("Great product!");
        reviewDto.setRating(5.0f);
        reviewDto.setUserId(1);
        reviewDto.setProductId(1);

        ReviewDto reviewDto2 = new ReviewDto();
        reviewDto2.setId(2);
        reviewDto2.setComment("Good quality");
        reviewDto2.setRating(4.0f);

        reviewList = Arrays.asList(reviewDto, reviewDto2);
    }
    @AfterEach
    void tearDown() {
        clearInvocations(reviewService);
    }


    @Test
    @WithMockUser
    void getAllReviews_ShouldReturnReviewList() throws Exception {
        // Arrange
        when(reviewService.getAllReviews()).thenReturn(reviewList);

        // Act & Assert
        mockMvc.perform(get("/review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].comment").value("Great product!"))
                .andExpect(jsonPath("$[1].comment").value("Good quality"));

        verify(reviewService, times(1)).getAllReviews();
    }

    @Test
    @WithMockUser
    void getAllByProductId_ShouldReturnReviews_WhenExists() throws Exception {
        // Arrange
        when(reviewService.getAllByProductId(1)).thenReturn(reviewList);

        // Act & Assert
        mockMvc.perform(get("/review/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reviewService, times(1)).getAllByProductId(1);
    }

    @Test
    @WithMockUser
    void getAllByProductId_ShouldReturnNoContent_WhenEmpty() throws Exception {
        // Arrange
        when(reviewService.getAllByProductId(999)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/review/product/999"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).getAllByProductId(999);
    }

    @Test
    @WithMockUser
    void getAllByProductIdandRating_ShouldReturnFilteredReviews() throws Exception {
        // Arrange
        when(reviewService.getAllByProductIdandRating(1, 5.0f)).thenReturn(Arrays.asList(reviewDto));

        // Act & Assert
        mockMvc.perform(get("/review/product/1/rating")
                        .param("rating", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(5.0));

        verify(reviewService, times(1)).getAllByProductIdandRating(1, 5.0f);
    }

    @Test
    @WithMockUser
    void getAllByProductIdandRating_ShouldReturnNoContent_WhenEmpty() throws Exception {
        // Arrange
        when(reviewService.getAllByProductIdandRating(1, 5.0f)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/review/product/1/rating")
                        .param("rating", "5.0"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).getAllByProductIdandRating(1, 5.0f);
    }

    @Test
    @WithMockUser
    void getAllByUserId_ShouldReturnUserReviews() throws Exception {
        // Arrange
        when(reviewService.getAllByUserId(1)).thenReturn(reviewList);

        // Act & Assert
        mockMvc.perform(get("/review/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reviewService, times(1)).getAllByUserId(1);
    }

    @Test
    @WithMockUser
    void getAllByUserId_ShouldReturnNoContent_WhenEmpty() throws Exception {
        // Arrange
        when(reviewService.getAllByUserId(999)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/review/user/999"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).getAllByUserId(999);
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedReview() throws Exception {
        // Arrange
        when(reviewService.create(any(ReviewDto.class))).thenReturn(reviewDto);

        // Act & Assert
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("Great product!"))
                .andExpect(jsonPath("$.rating").value(5.0));

        verify(reviewService, times(1)).create(any(ReviewDto.class));
    }

    @Test
    @WithMockUser
    void create_ShouldReturn201Status() throws Exception {
        // Arrange
        when(reviewService.create(any(ReviewDto.class))).thenReturn(reviewDto);

        // Act & Assert
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedReview_WhenExists() throws Exception {
        // Arrange
        ReviewDto updatedDto = new ReviewDto();
        updatedDto.setId(1);
        updatedDto.setComment("Updated comment");
        updatedDto.setRating(4.5f);

        when(reviewService.update(eq(1), any(ReviewDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/review/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated comment"))
                .andExpect(jsonPath("$.rating").value(4.5));

        verify(reviewService, times(1)).update(eq(1), any(ReviewDto.class));
    }

    @Test
    @WithMockUser
    void update_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(reviewService.update(eq(999), any(ReviewDto.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/review/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).update(eq(999), any(ReviewDto.class));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        // Arrange
        doNothing().when(reviewService).delete(1);

        // Act & Assert
        mockMvc.perform(delete("/review/1"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).delete(1);
    }

    @Test
    @WithMockUser
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        doThrow(new ReviewNotFoundException("Review not found")).when(reviewService).delete(999);

        // Act & Assert
        mockMvc.perform(delete("/review/999"))
                .andExpect(status().isNotFound());

        verify(reviewService, times(1)).delete(999);
    }

    @Test
    @WithMockUser
    void create_ShouldCallServiceMethod() throws Exception {
        // Arrange
        when(reviewService.create(any(ReviewDto.class))).thenReturn(reviewDto);

        // Act
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated());

        // Assert
        verify(reviewService, times(1)).create(any(ReviewDto.class));
    }

    @Test
    @WithMockUser
    void getAllByProductIdandRating_ShouldAcceptFloatParameter() throws Exception {
        // Arrange
        when(reviewService.getAllByProductIdandRating(1, 4.5f)).thenReturn(reviewList);

        // Act & Assert
        mockMvc.perform(get("/review/product/1/rating")
                        .param("rating", "4.5"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).getAllByProductIdandRating(1, 4.5f);
    }
}
