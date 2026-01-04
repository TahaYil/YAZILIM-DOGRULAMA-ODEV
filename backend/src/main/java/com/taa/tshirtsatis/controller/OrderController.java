package com.taa.tshirtsatis.controller;

import com.taa.tshirtsatis.dto.OrderDto;
import com.taa.tshirtsatis.dto.OrderProductDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id) {
        OrderDto o = orderService.getOrderById(id);
        if (o == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(o);
    }

    

    @GetMapping("/active-order")
    public ResponseEntity<OrderDto> getActiveOrder(Authentication authentication) {
        int userId = ((Users)authentication.getPrincipal()).getId();
        
        OrderDto activeOrder = orderService.getActiveOrderByUserId(userId);
        
        if (activeOrder != null) {
            return ResponseEntity.ok(activeOrder);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/create-active-order")
    public ResponseEntity<OrderDto> createActiveOrder(
        Authentication authentication, 
        @RequestBody OrderProductDto orderProductDto
    ) {
        int userId = ((Users)authentication.getPrincipal()).getId();
        
        OrderDto newOrder = orderService.createOrUpdateActiveOrder(userId, orderProductDto);
        
        return ResponseEntity.ok(newOrder);
    }

    @PutMapping("/{orderId}/add-product")
    public ResponseEntity<OrderDto> addProductToOrder(
        @PathVariable int orderId,
        @RequestBody OrderProductDto orderProductDto,
        Authentication authentication
    ) {
        int userId = ((Users)authentication.getPrincipal()).getId();
        
        OrderDto updatedOrder = orderService.addProductToOrder(orderId, orderProductDto, userId);
        
        return ResponseEntity.ok(updatedOrder);
    }
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable int id, @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderDto>> getActiveOrders() {
        return ResponseEntity.ok(orderService.getActiveOrders());
    }
}