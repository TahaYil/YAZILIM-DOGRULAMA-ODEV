package com.taa.tshirtsatis.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
    private Long userId; 

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }
    

}
