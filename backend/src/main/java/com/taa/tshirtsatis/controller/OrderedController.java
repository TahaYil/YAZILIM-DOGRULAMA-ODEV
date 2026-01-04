package com.taa.tshirtsatis.controller;

import com.taa.tshirtsatis.dto.OrderedDto;
import com.taa.tshirtsatis.service.OrderedService;
import com.taa.tshirtsatis.enums.OrderedState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/ordered")
@RequiredArgsConstructor
public class OrderedController {
    private final OrderedService orderedService;

    @PostMapping
    public ResponseEntity<OrderedDto> createOrdered(@RequestBody OrderedDto orderedDto) {
        OrderedDto createdOrder = orderedService.createOrdered(orderedDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED); // 201 Created
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<OrderedDto> getOrdered(@PathVariable int id) {
        return ResponseEntity.ok(orderedService.getOrderedById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderedDto>> getOrderedByUser(@PathVariable int userId) {
        return ResponseEntity.ok(orderedService.getOrderedByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderedDto>> getAllOrdered() {
        return ResponseEntity.ok(orderedService.getAllOrdered());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderedDto> updateOrdered(@PathVariable int id, @RequestBody OrderedDto orderedDto) {
        return ResponseEntity.ok(orderedService.updateOrdered(id, orderedDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdered(@PathVariable int id) {
        orderedService.deleteOrdered(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<OrderedDto> updateOrderedState(@PathVariable int id, @RequestParam OrderedState state) {
        return ResponseEntity.ok(orderedService.updateOrderState(id, state));
    }

//    @GetMapping("/state/{state}")
//    public ResponseEntity<List<OrderedDto>> getOrderedByState(@PathVariable OrderedState state) {
//        return ResponseEntity.ok(orderedService.getOrderedByState(state));
//    }

    @GetMapping("/state/{orderedId}")
    public ResponseEntity<OrderedState> getOrderedState(@PathVariable int orderedId) {
        OrderedDto orderedDto = orderedService.getOrderedById(orderedId);
        return ResponseEntity.ok(orderedDto.getState());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderedDto>> getOrderedByDate(@PathVariable Date date) {
        return ResponseEntity.ok(orderedService.getOrderedByDate(date));
    }

    @GetMapping("/today")
    public ResponseEntity<List<OrderedDto>> getTodaysOrders() {
        return ResponseEntity.ok(orderedService.getTodaysOrders());
    }

    @GetMapping("/count/{state}")
    public ResponseEntity<Long> countOrdersByState(@PathVariable OrderedState state) {
        return ResponseEntity.ok(orderedService.countOrdersByState(state));
    }

    @GetMapping("/{id}/delivered")
    public ResponseEntity<Boolean> isDelivered(@PathVariable int id) {
        return ResponseEntity.ok(orderedService.isDelivered(id));
    }
}
