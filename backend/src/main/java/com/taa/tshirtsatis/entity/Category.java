package com.taa.tshirtsatis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "products")
@EqualsAndHashCode(exclude = "products") 
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products;
}
