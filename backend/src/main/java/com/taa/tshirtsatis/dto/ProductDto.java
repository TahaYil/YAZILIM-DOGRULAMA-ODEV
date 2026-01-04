package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Category;
import com.taa.tshirtsatis.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private int id;

    @NotBlank(message = "Ürün adı boş olamaz.")
    private String name;

    private String description;

    @PositiveOrZero(message = "Fiyat negatif olamaz.")
    private float price;

    @Min(value = 0, message = "Miktar 0'dan az olamaz.")
    private int quantity;

    private byte[] image;

    @NotNull(message = "Kategori ID'leri boş olamaz.")
    private Set<Integer> categoryIds;

    private Map<String, Integer> sizeStocks;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.image = product.getImage();

        Set<Category> safeCategories = new HashSet<>(product.getCategories());
        this.categoryIds = safeCategories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        if (product.getSizes() != null) {
            this.sizeStocks = product.getSizes().stream()
                .collect(Collectors.toMap(
                    s -> s.getSize(),
                    s -> s.getStock()
                ));
        }
    }
}
