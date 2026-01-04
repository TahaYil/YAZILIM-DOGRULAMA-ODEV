package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.ReviewDto;
import com.taa.tshirtsatis.entity.Review;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.repository.ReviewRepository;
import com.taa.tshirtsatis.repository.UsersRepository;

import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.exception.ReviewNotFoundException;

import com.taa.tshirtsatis.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;

    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(ReviewDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewDto getReviewById(int id) {
        return reviewRepository.findById(id)
                .map(ReviewDto::new)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
    }

    //DEĞİŞEN
    @Transactional(readOnly = true)
    public List<ReviewDto> getAllByProductId(int productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
        return reviewRepository.getAllProductId(productId).stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }
    //DEĞİŞEN
    @Transactional(readOnly = true)
    public List<ReviewDto> getAllByUserId(int userId) {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return reviewRepository.getAllUserId(userId).stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }
    //EKLENEN
    public List<ReviewDto> getAllByProductIdandRating(int productId, float rating) {
        if (!productRepository.existsById(productId)) {
            throw new UserNotFoundException("User not found with id: " + productId);
        }
        return reviewRepository.getAllProductAndRating(productId, rating).stream()
                .map(ReviewDto::new)
                .collect(Collectors.toList());
    }

    public ReviewDto create(ReviewDto reviewDto) {
        Users user = usersRepository.findById(reviewDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + reviewDto.getUserId()));
        
        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + reviewDto.getProductId()));

        Review review = new Review();
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);
        return new ReviewDto(savedReview);
    }

    public ReviewDto update(int id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
        
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        
        Review updatedReview = reviewRepository.save(review);
        return new ReviewDto(updatedReview);
    }

    public void delete(int id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
