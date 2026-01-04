package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(int userId);
    
    List<Order> findByActiveTrue();
    
    Optional<Order> findByUser_IdAndActiveTrue(int userId);

    List<Order> findAllByUser_IdAndActiveTrue(int userId);
    
}