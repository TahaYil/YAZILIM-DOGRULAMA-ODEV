package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.ProductDto;
import com.taa.tshirtsatis.entity.Category;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.ProductSize;
import com.taa.tshirtsatis.exception.CategoryNotFoundException;
import com.taa.tshirtsatis.exception.InvalidProductDtoException;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.repository.CategoryRepository;
import com.taa.tshirtsatis.repository.ProductRepository;
import com.taa.tshirtsatis.repository.ProductSizeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductSizeRepository productSizeRepository;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("T-Shirts");

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(29.99f);
        product.setQuantity(10);
        product.setCategories(new HashSet<>(Arrays.asList(category)));

        productDto = new ProductDto();
        productDto.setId(1);
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(29.99f);
        productDto.setCategoryIds(new HashSet<>(Arrays.asList(1)));

        Map<String, Integer> sizeStocks = new HashMap<>();
        sizeStocks.put("S", 5);
        sizeStocks.put("M", 10);
        sizeStocks.put("L", 15);
        productDto.setSizeStocks(sizeStocks);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        // Act
        List<ProductDto> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenExists() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Act
        ProductDto result = productService.getProductById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999));
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void createProduct_ShouldCreateSuccessfully() throws IOException {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        // Act
        ProductDto result = productService.createProduct(productDto, mockFile);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1);
        verify(productRepository, atLeastOnce()).save(any(Product.class));
        verify(productSizeRepository, times(3)).save(any(ProductSize.class));
    }

    @Test
    void createProduct_WithoutFile_ShouldCreateSuccessfully() throws IOException {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto result = productService.createProduct(productDto, null);

        // Assert
        assertNotNull(result);
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldUpdateSuccessfully_WhenExists() throws IOException {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productSizeRepository.findByProduct(any(Product.class))).thenReturn(new ArrayList<>());

        // Act
        ProductDto result = productService.updateProduct(1, productDto, null);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductDtoIsNull() {
        // Act & Assert
        assertThrows(InvalidProductDtoException.class,
                () -> productService.updateProduct(1, null, null));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(999, productDto, null));
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void updateProductSizeStock_ShouldUpdateSuccessfully() {
        // Arrange
        ProductSize productSize = new ProductSize();
        productSize.setSize("M");
        productSize.setStock(10);
        productSize.setProduct(product);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productSizeRepository.findByProductAndSize(product, "M")).thenReturn(Optional.of(productSize));
        when(productSizeRepository.findByProduct(product)).thenReturn(Arrays.asList(productSize));
        when(productSizeRepository.save(any(ProductSize.class))).thenReturn(productSize);

        // Act
        productService.updateProductSizeStock(1, "M", 20);

        // Assert
        verify(productRepository, times(1)).findById(1);
        verify(productSizeRepository, times(1)).save(any(ProductSize.class));
    }

    @Test
    void getProductSizes_ShouldReturnSizes_WhenProductExists() {
        // Arrange
        ProductSize size = new ProductSize();
        size.setSize("M");
        size.setStock(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productSizeRepository.findByProduct(product)).thenReturn(Arrays.asList(size));

        // Act
        List<ProductSize> result = productService.getProductSizes(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void deleteProduct_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        when(productRepository.existsById(1)).thenReturn(true);

        // Act
        productService.deleteProduct(1);

        // Assert
        verify(productRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(productRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999));
        verify(productRepository, never()).deleteById(anyInt());
    }

    @Test
    void getProductImage_ShouldReturnImage_WhenExists() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        product.setImage(imageBytes);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Act
        byte[] result = productService.getProductImage(1);

        // Assert
        assertNotNull(result);
        assertArrayEquals(imageBytes, result);
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void findByCategoryId_ShouldReturnProducts() {
        // Arrange
        when(productRepository.findByCategoryId(1)).thenReturn(Arrays.asList(product));

        // Act
        List<Product> result = productService.findByCategoryId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategoryId(1);
    }

    @Test
    void findByCategoryName_ShouldReturnProducts() {
        // Arrange
        when(productRepository.findByCategoryName("T-Shirts")).thenReturn(Arrays.asList(product));

        // Act
        List<Product> result = productService.findByCategoryName("T-Shirts");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategoryName("T-Shirts");
    }

    @Test
    void findByCategoryName_ShouldThrowException_WhenNameIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productService.findByCategoryName(null));
        verify(productRepository, never()).findByCategoryName(anyString());
    }

    @Test
    void findByCategoryName_ShouldThrowException_WhenNameIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productService.findByCategoryName(""));
        verify(productRepository, never()).findByCategoryName(anyString());
    }
}

