package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.OrderDto;
import com.taa.tshirtsatis.dto.OrderProductDto;
import com.taa.tshirtsatis.entity.Order;
import com.taa.tshirtsatis.entity.Product;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.exception.OrderNotFoundException;
import com.taa.tshirtsatis.exception.ProductNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.OrderRepository;
import com.taa.tshirtsatis.repository.ProductRepository;
import com.taa.tshirtsatis.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(int id) {
        return orderRepository.findById(id)
                .map(OrderDto::new)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public OrderDto getActiveOrderByUserId(int userId) {
        Optional<Order> activeOrder = orderRepository.findByUser_IdAndActiveTrue(userId);
        return activeOrder.map(OrderDto::new).orElse(null);
    }

     public OrderDto createOrUpdateActiveOrder(int userId, OrderProductDto orderProductDto) {
        if (orderProductDto == null) {
            throw new IllegalArgumentException("Order product details cannot be null");
        }

        List<Order> activeOrders = orderRepository.findAllByUser_IdAndActiveTrue(userId);
        activeOrders.forEach(order -> order.setActive(false));
        orderRepository.saveAll(activeOrders);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(orderProductDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + orderProductDto.getProductId()));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setActive(true);
        newOrder.setAddress(""); 
        
        if (newOrder.getProducts() == null) {
            newOrder.setProducts(new HashSet<>());
        }
        
        newOrder.getProducts().add(product);
        
        int quantity = orderProductDto.getQuantity() > 0 ? orderProductDto.getQuantity() : 1;
        newOrder.setTotalPrice(product.getPrice() * quantity);

        Order savedOrder = orderRepository.save(newOrder);
        return new OrderDto(savedOrder);
    }

      public OrderDto addProductToOrder(int orderId, OrderProductDto orderProductDto, int userId) {
        if (orderProductDto == null) {
            throw new IllegalArgumentException("Order product details cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getUser().getId() != userId || !order.getActive()) {
            throw new RuntimeException("Invalid order or unauthorized access");
        }

        Product product = productRepository.findById(orderProductDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + orderProductDto.getProductId()));

        if (order.getProducts() == null) {
            order.setProducts(new HashSet<>());
        }

        

        order.getProducts().add(product);
        
        int quantity = orderProductDto.getQuantity() > 0 ? orderProductDto.getQuantity() : 1;
        float currentTotal = order.getTotalPrice();
        float productTotal = product.getPrice() * quantity;
        order.setTotalPrice(currentTotal + productTotal);

        Order updatedOrder = orderRepository.save(order);
        return new OrderDto(updatedOrder);
    }

    public OrderDto createOrder(OrderDto orderDto) {
        Users user = usersRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + orderDto.getUserId()));

        Set<Product> products = orderDto.getProductIds().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId)))
                .collect(Collectors.toSet());

        Order order = new Order();
        order.setUser(user);
        order.setProducts(products);
        order.setAddress(orderDto.getAddress());
        order.setTotalPrice(calculateTotalPrice(products));
        order.setActive(true);

        Order savedOrder = orderRepository.save(order);
        return new OrderDto(savedOrder);
    }

    

    public OrderDto updateOrder(int id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        Users user = usersRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + orderDto.getUserId()));

        Set<Product> products = orderDto.getProductIds().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId)))
                .collect(Collectors.toSet());

        order.setUser(user);
        order.setProducts(products);
        order.setAddress(orderDto.getAddress());
        order.setTotalPrice(calculateTotalPrice(products));
        order.setActive(orderDto.getActive());

        Order updatedOrder = orderRepository.save(order);
        return new OrderDto(updatedOrder);
    }

    public void deleteOrder(int id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getActiveOrders() {
        return orderRepository.findByActiveTrue().stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    private float calculateTotalPrice(Set<Product> products) {
        return (float) products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
}