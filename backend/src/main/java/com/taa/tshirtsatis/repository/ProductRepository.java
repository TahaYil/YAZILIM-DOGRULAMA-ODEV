package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Kategori ID'ye göre ürünleri bulmak için
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") int categoryId);

    // Kategori adı ile ürünleri bulmak için
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);
}
