package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    
    private int id;

    @NotBlank(message = "Yorum boş olamaz.")
    @Size(max = 500, message = "Yorum 500 karakterden uzun olamaz.")
    private String comment;

    @Min(value = 1, message = "Puan en az 1 olmalı.")
    @Max(value = 5, message = "Puan en fazla 5 olabilir.")
    private float rating;

    @Positive(message = "Kullanıcı ID'si pozitif olmalıdır.")
    private int userId;

    @Positive(message = "Ürün ID'si pozitif olmalıdır.")
    private int productId;

    public ReviewDto(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.userId = review.getUser().getId();
        this.productId = review.getProduct().getId();
    }
}
