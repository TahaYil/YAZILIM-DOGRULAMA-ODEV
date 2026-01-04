package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProduct(Product product);
    Optional<ProductSize> findByProductAndSize(Product product, String size);
} 