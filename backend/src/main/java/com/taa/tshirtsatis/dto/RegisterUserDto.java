package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {
	private String email;
    
    private String password;
    
    private Gender gender;
    
    private Role role;
}
