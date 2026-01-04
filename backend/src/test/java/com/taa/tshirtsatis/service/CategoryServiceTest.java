package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.CategoryDto;
import com.taa.tshirtsatis.entity.Category;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.exception.CategoryNotFoundException;
import com.taa.tshirtsatis.repository.CategoryRepository;
import com.taa.tshirtsatis.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("T-Shirts");
        category.setProducts(new HashSet<>());

        categoryDto = new CategoryDto();
        categoryDto.setId(1);
        categoryDto.setName("T-Shirts");

        categoryList = Arrays.asList(category);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("T-Shirts", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenExists() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Act
        CategoryDto result = categoryService.getCategoryById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("T-Shirts", result.getName());
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(999));
        verify(categoryRepository, times(1)).findById(999);
    }

    @Test
    void createCategory_ShouldCreateSuccessfully() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryDto result = categoryService.createCategory(categoryDto);

        // Assert
        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateSuccessfully_WhenExists() {
        // Arrange
        CategoryDto updatedDto = new CategoryDto();
        updatedDto.setName("Updated Category");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryDto result = categoryService.updateCategory(1, updatedDto);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(999, categoryDto));
        verify(categoryRepository, times(1)).findById(999);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(categoryRepository.existsById(1)).thenReturn(true);

        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(categoryRepository, times(1)).existsById(1);
        verify(categoryRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(999));
        verify(categoryRepository, times(1)).existsById(999);
        verify(categoryRepository, never()).deleteById(anyInt());
    }

    @Test
    void getProductCount_ShouldReturnCount_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryRepository.productCountByCategoryId(1)).thenReturn(5L);

        // Act
        Long count = categoryService.getProductCount(1);

        // Assert
        assertEquals(5L, count);
        verify(categoryRepository, times(1)).existsById(1);
        verify(categoryRepository, times(1)).productCountByCategoryId(1);
    }

    @Test
    void getProductCount_ShouldThrowException_WhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getProductCount(999));
        verify(categoryRepository, times(1)).existsById(999);
    }

    @Test
    void deleteCategoryWithProducts_ShouldDeleteCategoryAndProducts() {
        // Arrange
        Product product = new Product();
        product.setId(1);
        product.setCategories(new HashSet<>(Arrays.asList(category)));
        category.getProducts().add(product);

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategoryWithProducts(1);

        // Assert
        verify(categoryRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(product);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryWithProducts_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategoryWithProducts(999));
        verify(categoryRepository, times(1)).findById(999);
    }
}

