package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Order;
import com.taa.tshirtsatis.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDto {
    private int id;
    private int userId;
    private List<Integer> productIds;
    private float totalPrice;
    private String address;
    private Boolean active;

    // Varsayılan constructor
    public OrderDto() {}

    // Var olan Order entity'sinden constructor
    public OrderDto(Order order) {
        if (order == null) return;

        this.id = order.getId();
        this.userId = order.getUser() != null ? order.getUser().getId() : 0;
        
        // Ürün ID'lerini maple
        this.productIds = order.getProducts() != null 
            ? order.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList()) 
            : new ArrayList<>();
        
        this.totalPrice = order.getTotalPrice();
        this.address = order.getAddress();
        this.active = order.getActive();
    }

    // Getter ve Setter metodları
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}