package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private int id;

    @NotBlank(message = "Kategori adı boş olamaz.")
    @Size(max = 50, message = "Kategori adı en fazla 50 karakter olabilir.")
    private String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
