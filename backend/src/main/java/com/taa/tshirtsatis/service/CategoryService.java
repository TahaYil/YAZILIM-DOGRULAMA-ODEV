package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.CategoryDto;
import com.taa.tshirtsatis.entity.Category;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.exception.CategoryNotFoundException;
import com.taa.tshirtsatis.repository.CategoryRepository;
import com.taa.tshirtsatis.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return new CategoryDto(category);
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDto(savedCategory);
    }

    public CategoryDto updateCategory(int id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        category.setName(categoryDto.getName());

        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDto(updatedCategory);
    }

    public void deleteCategory(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public Long getProductCount(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        return categoryRepository.productCountByCategoryId(id);
    }

    @Transactional
    public void deleteCategoryWithProducts(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        Set<Product> products = category.getProducts();

        for (Product product : products) {
            product.getCategories().remove(category);
            
            productRepository.delete(product);
        }

        categoryRepository.delete(category);
    }
}
