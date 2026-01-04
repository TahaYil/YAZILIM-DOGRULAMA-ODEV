package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.OrderedDto;
import com.taa.tshirtsatis.repository.UsersRepository;
import com.taa.tshirtsatis.repository.OrderRepository;
import com.taa.tshirtsatis.entity.Order;
import com.taa.tshirtsatis.entity.Ordered;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.OrderedState;
import com.taa.tshirtsatis.exception.OrderedNotFoundException;
import com.taa.tshirtsatis.exception.OrderNotFoundException;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.repository.OrderedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderedService {
    private final OrderedRepository orderedRepository;
    private final OrderRepository orderRepository;
    private final UsersRepository userRepository;

    @Transactional(readOnly = true)
    public List<OrderedDto> getAllOrdered() {
        return orderedRepository.findAll().stream()
                .map(OrderedDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderedDto getOrderedById(int id) {
        return orderedRepository.findById(id)
                .map(OrderedDto::new)
                .orElseThrow(() -> new OrderedNotFoundException("Ordered not found with id: " + id));
    }

    

    public OrderedDto createOrdered(OrderedDto orderedDto) {
        if (orderedDto == null) {
            throw new IllegalArgumentException("OrderedDto cannot be null");
        }

        Order order = orderRepository.findById(orderedDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderedDto.getOrderId()));
        
        Users user = userRepository.findById(orderedDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + orderedDto.getUserId()));

        Ordered ordered = new Ordered();
        ordered.setOrder(order);
        ordered.setUser(user);
        ordered.setDate(Date.valueOf(LocalDate.now()));
        ordered.setState(OrderedState.PENDING);

        Ordered savedOrdered = orderedRepository.save(ordered);
        return new OrderedDto(savedOrdered);
    }

    public OrderedDto updateOrdered(int id, OrderedDto orderedDto) {
        if (orderedDto == null) {
            throw new IllegalArgumentException("OrderedDto cannot be null");
        }

        Ordered ordered = orderedRepository.findById(id)
                .orElseThrow(() -> new OrderedNotFoundException("Ordered not found with id: " + id));

        Order order = orderRepository.findById(orderedDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderedDto.getOrderId()));
        
        Users user = userRepository.findById(orderedDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + orderedDto.getUserId()));

        ordered.setOrder(order);
        ordered.setUser(user);
        ordered.setDate(orderedDto.getDate());
        ordered.setState(orderedDto.getState());

        Ordered updatedOrdered = orderedRepository.save(ordered);
        return new OrderedDto(updatedOrdered);
    }

    public void deleteOrdered(int id) {
        if (!orderedRepository.existsById(id)) {
            throw new OrderedNotFoundException("Ordered not found with id: " + id);
        }
        orderedRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderedDto> getOrderedByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        return orderedRepository.findByUserId(userId).stream()
                .map(OrderedDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderedDto> getOrderedByState(OrderedState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        return orderedRepository.findByState(state).stream()
                .map(OrderedDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderedDto> getOrderedByDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return orderedRepository.findByDate(date).stream()
                .map(OrderedDto::new)
                .collect(Collectors.toList());
    }

    public OrderedDto updateOrderState(int id, OrderedState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("New state cannot be null");
        }

        Ordered ordered = orderedRepository.findById(id)
                .orElseThrow(() -> new OrderedNotFoundException("Ordered not found with id: " + id));

        // State geçiş kontrolü
        if (!isValidStateTransition(ordered.getState(), newState)) {
            throw new IllegalArgumentException("Invalid state transition from " + ordered.getState() + " to " + newState);
        }

        ordered.setState(newState);
        Ordered updatedOrdered = orderedRepository.save(ordered);
        return new OrderedDto(updatedOrdered);
    }

    @Transactional(readOnly = true)
    public boolean isDelivered(int id) {
        return orderedRepository.findById(id)
                .map(ordered -> ordered.getState() == OrderedState.DELIVERED)
                .orElseThrow(() -> new OrderedNotFoundException("Ordered not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderedDto> getTodaysOrders() {
        Date today = Date.valueOf(LocalDate.now());
        return getOrderedByDate(today);
    }

    @Transactional(readOnly = true)
    public long countOrdersByState(OrderedState state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        return orderedRepository.countByState(state);
    }

    private boolean isValidStateTransition(OrderedState currentState, OrderedState newState) {
        // State geçiş kuralları
        switch (currentState) {
            case PENDING:
                return newState == OrderedState.PROCESSING || newState == OrderedState.CANCELLED;
            case PROCESSING:
                return newState == OrderedState.SHIPPED || newState == OrderedState.CANCELLED;
            case SHIPPED:
                return newState == OrderedState.DELIVERED;
            case DELIVERED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
}
