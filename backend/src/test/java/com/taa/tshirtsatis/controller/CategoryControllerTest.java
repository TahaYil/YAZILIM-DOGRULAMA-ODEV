package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.CategoryDto;
import com.taa.tshirtsatis.exception.CategoryNotFoundException;
import com.taa.tshirtsatis.service.CategoryService;
import com.taa.tshirtsatis.service.JwtService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CategoryControllerTest.TestConfig.class)
class CategoryControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return mock(CategoryService.class);
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
    private CategoryService categoryService;

    private CategoryDto categoryDto;
    private List<CategoryDto> categoryList;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setId(1);
        categoryDto.setName("T-Shirts");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2);
        categoryDto2.setName("Hoodies");

        categoryList = Arrays.asList(categoryDto, categoryDto2);
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(categoryList);

        // Act & Assert
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("T-Shirts"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Hoodies"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getCategoryById_ShouldReturnCategory_WhenExists() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1)).thenReturn(categoryDto);

        // Act & Assert
        mockMvc.perform(get("/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("T-Shirts"));

        verify(categoryService, times(1)).getCategoryById(1);
    }

    @Test
    void getCategoryById_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999)).thenThrow(new CategoryNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/category/999"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getCategoryById(999);
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryDto);

        // Act & Assert
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("T-Shirts"));

        verify(categoryService, times(1)).createCategory(any(CategoryDto.class));
    }

    @Test
    void createCategory_ShouldReturn201Status() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryDto);

        // Act & Assert
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory_WhenExists() throws Exception {
        // Arrange
        CategoryDto updatedDto = new CategoryDto();
        updatedDto.setId(1);
        updatedDto.setName("Updated Category");

        when(categoryService.updateCategory(eq(1), any(CategoryDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/category/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"));

        verify(categoryService, times(1)).updateCategory(eq(1), any(CategoryDto.class));
    }

    @Test
    void updateCategory_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(999), any(CategoryDto.class)))
                .thenThrow(new CategoryNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(put("/category/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(eq(999), any(CategoryDto.class));
    }

    @Test
    void getProductCount_ShouldReturnCount_WhenCategoryExists() throws Exception {
        // Arrange
        when(categoryService.getProductCount(1)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/category/1/products/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(categoryService, times(1)).getProductCount(1);
    }

    @Test
    void getProductCount_ShouldReturn404_WhenCategoryNotFound() throws Exception {
        // Arrange
        when(categoryService.getProductCount(999)).thenThrow(new CategoryNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/category/999/products/count"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getProductCount(999);
    }

    @Test
    void deleteCategory_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategoryWithProducts(1);

        // Act & Assert
        mockMvc.perform(delete("/category/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategoryWithProducts(1);
    }

    @Test
    void deleteCategory_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        doThrow(new CategoryNotFoundException("Category not found"))
                .when(categoryService).deleteCategoryWithProducts(999);

        // Act & Assert
        mockMvc.perform(delete("/category/999"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).deleteCategoryWithProducts(999);
    }

    @Test
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategories() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
