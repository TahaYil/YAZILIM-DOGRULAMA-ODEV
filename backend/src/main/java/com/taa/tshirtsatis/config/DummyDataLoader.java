package com.taa.tshirtsatis.config;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.repository.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DummyDataLoader {
    @Bean
    public CommandLineRunner loadDummyUser(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "admin@example.com";
            if (!usersRepository.existsByEmail(email)) {
                Users user = new Users();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setRole(Role.ADMIN);
                user.setGender(Gender.MALE);
                usersRepository.save(user);
                System.out.println("Dummy admin user created: " + email + " / admin123");
            }
        };
    }
}
