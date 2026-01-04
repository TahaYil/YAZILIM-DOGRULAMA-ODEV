package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.LoginUserDto;
import com.taa.tshirtsatis.dto.RegisterUserDto;
import com.taa.tshirtsatis.entity.Users;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.service.AuthenticationService;
import com.taa.tshirtsatis.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthenticationControllerTest.TestConfig.class)
class AuthenticationControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtService jwtService() {
            return mock(JwtService.class);
        }
        @Bean
        public AuthenticationService authenticationService() {
            return mock(AuthenticationService.class);
        }
        @Bean
        public UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    private Users user;
    private RegisterUserDto registerUserDto;
    private LoginUserDto loginUserDto;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setGender(Gender.MALE);
        user.setRole(Role.USER);

        registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("test@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setGender(Gender.MALE);

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password123");
    }
    @AfterEach
    void tearDown() {
        clearInvocations(authenticationService, jwtService);
    }


    @Test
    void register_ShouldReturnRegisteredUser_WhenValidInput() throws Exception {
        // Arrange
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.gender").value("MALE"));

        verify(authenticationService, times(1)).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_ShouldCallAuthenticationService() throws Exception {
        // Arrange
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        // Act
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isOk());

        // Assert
        verify(authenticationService, times(1)).signup(any(RegisterUserDto.class));
    }

    @Test
    void authenticate_ShouldReturnLoginResponse_WhenValidCredentials() throws Exception {
        // Arrange
        String token = "test.jwt.token";
        long expiresIn = 3600000L;

        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(any(Users.class))).thenReturn(token);
        when(jwtService.getExpirationTime()).thenReturn(expiresIn);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.expiresIn").value(expiresIn))
                .andExpect(jsonPath("$.userId").value(1));

        verify(authenticationService, times(1)).authenticate(any(LoginUserDto.class));
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).getExpirationTime();
    }

    @Test
    void authenticate_ShouldGenerateJwtToken() throws Exception {
        // Arrange
        String token = "generated.jwt.token";
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(any(Users.class))).thenReturn(token);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void authenticate_ShouldReturnUserIdInResponse() throws Exception {
        // Arrange
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(any(Users.class))).thenReturn("token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void register_ShouldReturnUserWithCorrectRole() throws Exception {
        // Arrange
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
