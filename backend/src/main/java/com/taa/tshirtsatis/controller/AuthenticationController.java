package com.taa.tshirtsatis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.taa.tshirtsatis.controller.LoginResponse;

import com.taa.tshirtsatis.dto.LoginUserDto;
import com.taa.tshirtsatis.dto.RegisterUserDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.service.AuthenticationService;
import com.taa.tshirtsatis.service.JwtService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> register(@RequestBody RegisterUserDto registerUserDto) {
        Users registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Users authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserId((long) authenticatedUser.getId());


        return ResponseEntity.ok(loginResponse);
    }
}
