package com.taa.tshirtsatis;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({ "docker", "dev" }) // PROD’da istemiyorsan önemli
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin kullanıcısı ekle
        if (usersRepository.findByEmail("admin@admin.com").isEmpty()) {
            Users adminUser = Users.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .build();
            usersRepository.save(adminUser);
        }
    }
}