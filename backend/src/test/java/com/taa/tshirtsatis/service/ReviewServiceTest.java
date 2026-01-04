package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.ReviewDto;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.Review;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.exception.ReviewNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.ProductRepository;
import com.taa.tshirtsatis.repository.ReviewRepository;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private ReviewDto reviewDto;
    private Users user;
    private Product product;

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

        review = new Review();
        review.setId(1);
        review.setComment("Great product!");
        review.setRating(5.0f);
        review.setUser(user);
        review.setProduct(product);

        reviewDto = new ReviewDto();
        reviewDto.setId(1);
        reviewDto.setComment("Great product!");
        reviewDto.setRating(5.0f);
        reviewDto.setUserId(1);
        reviewDto.setProductId(1);
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        // Arrange
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(review));

        // Act
        List<ReviewDto> result = reviewService.getAllReviews();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great product!", result.get(0).getComment());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReviewById_ShouldReturnReview_WhenExists() {
        // Arrange
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        // Act
        ReviewDto result = reviewService.getReviewById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Great product!", result.getComment());
        assertEquals(5.0f, result.getRating());
        verify(reviewRepository, times(1)).findById(1);
    }

    @Test
    void getReviewById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(reviewRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(999));
        verify(reviewRepository, times(1)).findById(999);
    }

    @Test
    void getAllByProductId_ShouldReturnReviews_WhenProductExists() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);
        when(reviewRepository.getAllProductId(1)).thenReturn(Arrays.asList(review));

        // Act
        List<ReviewDto> result = reviewService.getAllByProductId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).existsById(1);
        verify(reviewRepository, times(1)).getAllProductId(1);
    }

    @Test
    void getAllByProductId_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> reviewService.getAllByProductId(999));
        verify(productRepository, times(1)).existsById(999);
    }

    @Test
    void getAllByUserId_ShouldReturnReviews_WhenUserExists() {
        // Arrange
        when(usersRepository.existsById(1)).thenReturn(true);
        when(reviewRepository.getAllUserId(1)).thenReturn(Arrays.asList(review));

        // Act
        List<ReviewDto> result = reviewService.getAllByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usersRepository, times(1)).existsById(1);
        verify(reviewRepository, times(1)).getAllUserId(1);
    }

    @Test
    void getAllByUserId_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> reviewService.getAllByUserId(999));
        verify(usersRepository, times(1)).existsById(999);
    }

    @Test
    void getAllByProductIdandRating_ShouldReturnReviews() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);
        when(reviewRepository.getAllProductAndRating(1, 5.0f)).thenReturn(Arrays.asList(review));

        // Act
        List<ReviewDto> result = reviewService.getAllByProductIdandRating(1, 5.0f);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).existsById(1);
        verify(reviewRepository, times(1)).getAllProductAndRating(1, 5.0f);
    }

    @Test
    void getAllByProductIdandRating_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> reviewService.getAllByProductIdandRating(999, 5.0f));
        verify(productRepository, times(1)).existsById(999);
    }

    @Test
    void create_ShouldCreateReviewSuccessfully() {
        // Arrange
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewDto result = reviewService.create(reviewDto);

        // Assert
        assertNotNull(result);
        assertEquals("Great product!", result.getComment());
        assertEquals(5.0f, result.getRating());
        verify(usersRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findById(1);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> reviewService.create(reviewDto));
        verify(usersRepository, times(1)).findById(1);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> reviewService.create(reviewDto));
        verify(productRepository, times(1)).findById(1);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void update_ShouldUpdateReviewSuccessfully_WhenExists() {
        // Arrange
        ReviewDto updateDto = new ReviewDto();
        updateDto.setComment("Updated comment");
        updateDto.setRating(4.0f);

        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewDto result = reviewService.update(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void update_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(reviewRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.update(999, reviewDto));
        verify(reviewRepository, times(1)).findById(999);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void delete_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(reviewRepository.existsById(1)).thenReturn(true);

        // Act
        reviewService.delete(1);

        // Assert
        verify(reviewRepository, times(1)).existsById(1);
        verify(reviewRepository, times(1)).deleteById(1);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(reviewRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, () -> reviewService.delete(999));
        verify(reviewRepository, times(1)).existsById(999);
        verify(reviewRepository, never()).deleteById(anyInt());
    }
}

