package com.taa.tshirtsatis.entity;

import com.taa.tshirtsatis.enums.OrderedState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordered")
public class Ordered {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(mappedBy = "ordered")
    private Set<Review> reviews;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;

    private Date date;

    @Column(name = "state", columnDefinition = "VARCHAR(255) DEFAULT 'PENDING' CHECK (state IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'))")
    @Enumerated(EnumType.STRING)
    private OrderedState state = OrderedState.PENDING;

   
}
