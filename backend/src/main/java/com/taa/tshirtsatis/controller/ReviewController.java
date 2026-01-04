package com.taa.tshirtsatis.controller;

import com.taa.tshirtsatis.dto.ReviewDto;
import com.taa.tshirtsatis.exception.ReviewNotFoundException;
import com.taa.tshirtsatis.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> list=reviewService.getAllReviews();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDto>> getAllByProductId(@PathVariable int productId) {
        List<ReviewDto> reviews = reviewService.getAllByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content verecek
        }
        return ResponseEntity.ok(reviews); // 200 OK 
    }
    //EKLENDİ
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<List<ReviewDto>> getAllByProductIdandRating(@PathVariable int productId,
                                                                      @RequestParam float rating) {
        List<ReviewDto> reviews = reviewService.getAllByProductIdandRating(productId, rating);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content verecek
        }
        return ResponseEntity.ok(reviews); // 200 OK
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getAllByUserId(@PathVariable int userId) {
        List<ReviewDto> reviews = reviewService.getAllByUserId(userId);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content 
        }
        return ResponseEntity.ok(reviews); // 200 OK 
    }

    @PostMapping
    public ResponseEntity<ReviewDto> create(@RequestBody ReviewDto reviewDto) {
        ReviewDto createdReview = reviewService.create(reviewDto);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED); // 201 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> update(@PathVariable int id, @RequestBody ReviewDto reviewDto) {
        ReviewDto updatedReview = reviewService.update(id, reviewDto);
        if (updatedReview == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(updatedReview); // 200 OK guncellerse
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            reviewService.delete(id);
            return ResponseEntity.noContent().build(); // 204 No Content verecek silerse
        } catch (ReviewNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        }
    }
}
