package com.taa.tshirtsatis;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (usersRepository.findByEmail("admin@admin.com").isEmpty()) {
                Users adminUser = Users.builder()
                        .email("admin@admin.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN)
                        .gender(Gender.MALE)
                        .build();
                usersRepository.save(adminUser);
                System.out.println("[DataInitializer] Admin user created: admin@admin.com / admin");
            } else {
                System.out.println("[DataInitializer] Admin user already exists: admin@admin.com");
            }
        } catch (Exception e) {
            System.err.println("[DataInitializer] Admin user could not be created! Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}