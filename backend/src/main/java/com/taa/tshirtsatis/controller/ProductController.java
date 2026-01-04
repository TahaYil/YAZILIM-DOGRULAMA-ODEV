package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.ProductDto;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable("id") int id) {
        byte[] image = productService.getProductImage(id);
        return image != null
                ? ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image)
                : ResponseEntity.notFound().build(); // 404 Not Found if image doesn't exist
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
        @RequestPart("product") String productJson,
        @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductDto productDto = objectMapper.readValue(productJson, ProductDto.class);

        ProductDto createdProduct = productService.createProduct(productDto, file);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
        ProductDto product = productService.getProductById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build(); // 404 product yoksa
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable int id,
                                                    @RequestPart("product") ProductDto productDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        System.out.println(productDto);
        ProductDto updatedProduct = productService.updateProduct(id, productDto, file);
        return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build(); // 404 product yoksa
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryId(@PathVariable int id) {
        List<Product> products = productService.findByCategoryId(id);
        List<ProductDto> productDtos = products.stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/category/name/{name}")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryName(@PathVariable String name) {
        List<Product> products = productService.findByCategoryName(name);
        List<ProductDto> productDtos = products.stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }
}
