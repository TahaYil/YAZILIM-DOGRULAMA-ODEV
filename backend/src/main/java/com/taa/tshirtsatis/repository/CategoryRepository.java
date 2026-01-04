package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository <Category ,Integer> {

    @Query("SELECT COUNT(p) FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    Long productCountByCategoryId (@Param("categoryId") Integer id);

}
