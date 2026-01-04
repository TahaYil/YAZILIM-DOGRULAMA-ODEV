package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    
    private int id;

    @Email(message = "Geçerli bir email adresi giriniz.")
    @NotBlank(message = "Email boş olamaz.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 1, message = "Şifre en az 1 karakter olmalıdır.")
    private String password;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    private Role role;

    public UsersDto(Users user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.gender = user.getGender();
        this.role = user.getRole();
    }
}
