package com.taa.tshirtsatis.controller;

import com.taa.tshirtsatis.dto.UsersDto;
import com.taa.tshirtsatis.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/all")
    public ResponseEntity<List<UsersDto>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable int id) {
        UsersDto user = usersService.getUserById(id); // Eğer UserNotFoundException atarsa, global handler yakalayacak.
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UsersDto> createUser(@Valid @RequestBody UsersDto usersDto) {
        UsersDto createdUser = usersService.createUser(usersDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDto> updateUser(@PathVariable int id, @Valid @RequestBody UsersDto usersDto) {
        System.out.println(id);
        UsersDto updatedUser = usersService.updateUser(id, usersDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
