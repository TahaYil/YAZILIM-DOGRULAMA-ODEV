package com.taa.tshirtsatis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String address;

    @Column(name = "total_price")
    private float totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "orders_products",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true; 

    @OneToOne(mappedBy = "order")
    private Ordered ordered;

    @PrePersist
    public void prePersist() {
        if (this.active == null) {
            this.active = true; 
        }
    }
}
