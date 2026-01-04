package com.taa.tshirtsatis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orders", "sizes"})
@EqualsAndHashCode(exclude = {"orders", "sizes"})
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private float price;

    @Column(name = "quantity")
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity; // Toplam stok miktarı

    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductSize> sizes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "products_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, 
              org.hibernate.annotations.CascadeType.MERGE,
              org.hibernate.annotations.CascadeType.REFRESH})
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private Set<Order> orders;

    @OneToMany(mappedBy = "product")
    private Set<Review> reviews;

    // Toplam stok miktarını güncellemek için
    public void updateTotalQuantity() {
        this.quantity = this.sizes.stream()
                .mapToInt(ProductSize::getStock)
                .sum();
    }
}
