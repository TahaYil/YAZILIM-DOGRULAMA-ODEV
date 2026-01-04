package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.ProductDto;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.taa.tshirtsatis.service.JwtService;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProductControllerTest.TestConfig.class)
class ProductControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return mock(ProductService.class);
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
    private ProductService productService;

    private ProductDto productDto;
    private Product product;
    private List<ProductDto> productList;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setId(1);
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(29.99f);
        productDto.setQuantity(10);
        productDto.setCategoryIds(new HashSet<>(Arrays.asList(1)));

        HashMap<String, Integer> sizeStocks = new HashMap<>();
        sizeStocks.put("S", 5);
        sizeStocks.put("M", 5);
        productDto.setSizeStocks(sizeStocks);

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(29.99f);

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2);
        productDto2.setName("Second Product");
        productDto2.setPrice(39.99f);

        productList = Arrays.asList(productDto, productDto2);
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[1].name").value("Second Product"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenExists() throws Exception {
        // Arrange
        when(productService.getProductById(1)).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(get("/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(29.99));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    @WithMockUser
    void getProductById_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/product/999")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        String productJson = objectMapper.writeValueAsString(productDto);
        MockMultipartFile productPart = new MockMultipartFile(
                "product", "", "application/json", productJson.getBytes()
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        when(productService.createProduct(any(ProductDto.class), any())).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(multipart("/product")
                        .file(productPart)
                        .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).createProduct(any(ProductDto.class), any());
    }

    @Test
    void createProduct_ShouldWorkWithoutImage() throws Exception {
        // Arrange
        String productJson = objectMapper.writeValueAsString(productDto);
        MockMultipartFile productPart = new MockMultipartFile(
                "product", "", "application/json", productJson.getBytes()
        );

        when(productService.createProduct(any(ProductDto.class), isNull())).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(multipart("/product")
                        .file(productPart))
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(ProductDto.class), isNull());
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1), any(ProductDto.class), any())).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(multipart("/product/1")
                        .file(new MockMultipartFile("product", "", "application/json",
                                objectMapper.writeValueAsString(productDto).getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService, times(1)).updateProduct(eq(1), any(ProductDto.class), any());
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1);

        // Act & Assert
        mockMvc.perform(delete("/product/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1);
    }


    @Test
    void getProductImage_ShouldReturnImage_WhenExists() throws Exception {
        // Arrange
        byte[] imageBytes = "test image content".getBytes();
        when(productService.getProductImage(1)).thenReturn(imageBytes);

        // Act & Assert
        mockMvc.perform(get("/product/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));

        verify(productService, times(1)).getProductImage(1);
    }

    @Test
    void getProductImage_ShouldReturn404_WhenImageNotFound() throws Exception {
        // Arrange
        when(productService.getProductImage(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/product/999/image"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductImage(999);
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProducts() throws Exception {
        // Arrange
        when(productService.findByCategoryId(1)).thenReturn(Arrays.asList(product));

        // Act & Assert
        mockMvc.perform(get("/product/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).findByCategoryId(1);
    }

    @Test
    void getProductsByCategoryName_ShouldReturnProducts() throws Exception {
        // Arrange
        when(productService.findByCategoryName("T-Shirts")).thenReturn(Arrays.asList(product));

        // Act & Assert
        mockMvc.perform(get("/product/category/name/T-Shirts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(productService, times(1)).findByCategoryName("T-Shirts");
    }

    @Test
    void getProductsByCategoryId_ShouldReturnEmptyList_WhenNoProducts() throws Exception {
        // Arrange
        when(productService.findByCategoryId(999)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/product/category/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
