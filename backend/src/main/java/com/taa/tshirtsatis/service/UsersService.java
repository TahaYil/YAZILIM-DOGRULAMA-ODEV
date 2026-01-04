package com.taa.tshirtsatis.service;

import com.taa.tshirtsatis.dto.UsersDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.repository.UsersRepository;

import com.taa.tshirtsatis.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    public Users processOAuthPostLogin(String email) {
        return usersRepository.findByEmail(email).orElseGet(() -> {
            Users user = new Users();
            user.setEmail(email);
            user.setRole(Role.USER);
            return usersRepository.save(user);
        });
    }


    @Transactional(readOnly = true)
    public List<UsersDto> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(UsersDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsersDto getUserById(int id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return new UsersDto(user);
    }

    public UsersDto createUser(UsersDto usersDto) {
        if (usersDto.getPassword() == null || usersDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        Users user = new Users();
        user.setEmail(usersDto.getEmail());
        user.setPassword(passwordEncoder.encode(usersDto.getPassword()));
        user.setGender(usersDto.getGender());
        user.setRole(usersDto.getRole());

        Users savedUser = usersRepository.save(user);
        return new UsersDto(savedUser);
    }

    public UsersDto updateUser(int id, UsersDto usersDto) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setEmail(usersDto.getEmail());
        if (usersDto.getPassword() != null && !usersDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(usersDto.getPassword()));
        }
        user.setGender(usersDto.getGender());
        user.setRole(usersDto.getRole());

        Users updatedUser = usersRepository.save(user);
        return new UsersDto(updatedUser);
    }

    public void deleteUser(int id) {
        if (!usersRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        usersRepository.deleteById(id);
    }
}
