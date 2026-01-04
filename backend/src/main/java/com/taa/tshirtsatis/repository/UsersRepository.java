package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    // Kullanıcıyı email ile bulur
    Optional<Users> findByEmail(String email);

    // Verilen email'e sahip bir kullanıcı var mı kontrolü yapar
    boolean existsByEmail(String email);
}
