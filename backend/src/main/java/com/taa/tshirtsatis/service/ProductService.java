package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.ProductDto;
import com.taa.tshirtsatis.entity.Category;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.ProductSize;
import com.taa.tshirtsatis.repository.CategoryRepository;
import com.taa.tshirtsatis.repository.ProductRepository;
import com.taa.tshirtsatis.repository.ProductSizeRepository;

import com.taa.tshirtsatis.exception.CategoryNotFoundException;
import com.taa.tshirtsatis.exception.InvalidProductDtoException;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSizeRepository productSizeRepository;

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(int id) {
        return productRepository.findById(id)
                .map(ProductDto::new)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public ProductDto createProduct(ProductDto productDto, MultipartFile file) throws IOException {
        Product product = new Product();

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(0); // Initialize with 0, will be updated based on sizes

        if (file != null && !file.isEmpty()) {
            byte[] imageBytes = file.getBytes();
            product.setImage(imageBytes);
        } else {
            product.setImage(null);
        }

        Set<Category> categories = new HashSet<>();
        for (Integer categoryId : productDto.getCategoryIds()) {
            categoryRepository.findById(categoryId).ifPresent(categories::add);
        }
        product.setCategories(categories);

        product = productRepository.save(product);

        // Create sizes with stock values from productDto
        if (productDto.getSizeStocks() != null) {
            for (Map.Entry<String, Integer> entry : productDto.getSizeStocks().entrySet()) {
                ProductSize productSize = new ProductSize();
                productSize.setProduct(product);
                productSize.setSize(entry.getKey());
                productSize.setStock(entry.getValue());
                productSizeRepository.save(productSize);
            }
        } else {
            // Create default sizes with 0 stock if no sizeStocks provided
            createDefaultSizes(product);
        }

        // Update total quantity
        updateTotalQuantity(product);

        return new ProductDto(product);
    }

    private void createDefaultSizes(Product product) {
        String[] sizes = {"S", "M", "L", "XL"};
        for (String size : sizes) {
            ProductSize productSize = new ProductSize();
            productSize.setProduct(product);
            productSize.setSize(size);
            productSize.setStock(0);
            productSizeRepository.save(productSize);
        }
    }

    public ProductDto updateProduct(int id, ProductDto productDto, MultipartFile file) throws IOException {
        if (productDto == null) {
            throw new InvalidProductDtoException("ProductDto cannot be null");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());

        if (file != null && !file.isEmpty()) {
            product.setImage(file.getBytes());
        }

        if (productDto.getCategoryIds() != null && !productDto.getCategoryIds().isEmpty()) {
            Set<Category> categories = productDto.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId)))
                    .collect(Collectors.toSet());
            product.setCategories(categories);
        }

        // Update size stocks
        if (productDto.getSizeStocks() != null) {
            // First, remove existing sizes
            productSizeRepository.findByProduct(product).forEach(productSizeRepository::delete);

            // Then, create new sizes with updated stock values
            for (Map.Entry<String, Integer> entry : productDto.getSizeStocks().entrySet()) {
                ProductSize productSize = new ProductSize();
                productSize.setProduct(product);
                productSize.setSize(entry.getKey());
                productSize.setStock(entry.getValue());
                productSizeRepository.save(productSize);
            }

            // Update total quantity
            updateTotalQuantity(product);
        }

        Product updatedProduct = productRepository.save(product);
        return new ProductDto(updatedProduct);
    }

    public void updateProductSizeStock(int productId, String size, int stock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        ProductSize productSize = productSizeRepository.findByProductAndSize(product, size)
                .orElseThrow(() -> new IllegalArgumentException("Size " + size + " not found for product"));

        productSize.setStock(stock);
        productSizeRepository.save(productSize);

        // Update total quantity
        updateTotalQuantity(product);
    }

    private void updateTotalQuantity(Product product) {
        int totalQuantity = productSizeRepository.findByProduct(product).stream()
                .mapToInt(ProductSize::getStock)
                .sum();
        product.setQuantity(totalQuantity);
        productRepository.save(product);
    }

    public List<ProductSize> getProductSizes(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        return productSizeRepository.findByProduct(product);
    }

    public void deleteProduct(int id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] getProductImage(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return product.getImage() != null ? product.getImage() : new byte[0];
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategoryId(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        return productRepository.findByCategoryName(categoryName);
    }
}
