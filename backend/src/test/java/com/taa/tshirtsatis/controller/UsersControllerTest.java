package com.taa.tshirtsatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taa.tshirtsatis.dto.UsersDto;
import com.taa.tshirtsatis.enums.Gender;
import com.taa.tshirtsatis.enums.Role;
import com.taa.tshirtsatis.exception.UserNotFoundException;
import com.taa.tshirtsatis.service.JwtService;
import com.taa.tshirtsatis.service.UsersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UsersControllerTest.TestConfig.class)
class UsersControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UsersService usersService() {
            return mock(UsersService.class);
        }
        @Bean
        public JwtService jwtService() {
            return mock(JwtService.class);
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
    private UsersService usersService;

    private UsersDto usersDto;
    private List<UsersDto> usersList;

    @BeforeEach
    void setUp() {
        usersDto = new UsersDto();
        usersDto.setId(1);
        usersDto.setEmail("test@example.com");
        usersDto.setPassword("password123");
        usersDto.setGender(Gender.MALE);
        usersDto.setRole(Role.USER);

        UsersDto usersDto2 = new UsersDto();
        usersDto2.setId(2);
        usersDto2.setEmail("test2@example.com");
        usersDto2.setGender(Gender.FEMALE);
        usersDto2.setRole(Role.ADMIN);

        usersList = Arrays.asList(usersDto, usersDto2);
    }
    @AfterEach
    void tearDown() {
        clearInvocations(usersService);
    }


    @Test
    @WithMockUser
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        // Arrange
        when(usersService.getAllUsers()).thenReturn(usersList);

        // Act & Assert
        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[1].email").value("test2@example.com"));

        verify(usersService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser
    void getUserById_ShouldReturnUser_WhenExists() throws Exception {
        // Arrange
        when(usersService.getUserById(1)).thenReturn(usersDto);

        // Act & Assert
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(usersService, times(1)).getUserById(1);
    }

    @Test
    @WithMockUser
    void getUserById_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(usersService.getUserById(999)).thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/user/999"))
                .andExpect(status().isNotFound());

        verify(usersService, times(1)).getUserById(999);
    }

    @Test
    @WithMockUser
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(usersService.createUser(any(UsersDto.class))).thenReturn(usersDto);

        // Act & Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(usersService, times(1)).createUser(any(UsersDto.class));
    }

    @Test
    @WithMockUser
    void createUser_ShouldReturn201Status() throws Exception {
        // Arrange
        when(usersService.createUser(any(UsersDto.class))).thenReturn(usersDto);

        // Act & Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void updateUser_ShouldReturnUpdatedUser_WhenExists() throws Exception {
        // Arrange
        UsersDto updatedDto = new UsersDto();
        updatedDto.setId(1);
        updatedDto.setEmail("updated@example.com");
        updatedDto.setPassword("newpassword");
        updatedDto.setGender(Gender.FEMALE);
        updatedDto.setRole(Role.ADMIN);

        when(usersService.updateUser(eq(1), any(UsersDto.class))).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(usersService, times(1)).updateUser(eq(1), any(UsersDto.class));
    }

    @Test
    @WithMockUser
    void updateUser_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        when(usersService.updateUser(eq(999), any(UsersDto.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/user/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersDto)))
                .andExpect(status().isNotFound());

        verify(usersService, times(1)).updateUser(eq(999), any(UsersDto.class));
    }

    @Test
    @WithMockUser
    void deleteUser_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        // Arrange
        doNothing().when(usersService).deleteUser(1);

        // Act & Assert
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());

        verify(usersService, times(1)).deleteUser(1);
    }

    @Test
    @WithMockUser
    void deleteUser_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        doThrow(new UserNotFoundException("User not found")).when(usersService).deleteUser(999);

        // Act & Assert
        mockMvc.perform(delete("/user/999"))
                .andExpect(status().isNotFound());

        verify(usersService, times(1)).deleteUser(999);
    }

    @Test
    @WithMockUser
    void createUser_ShouldAcceptAllUserFields() throws Exception {
        // Arrange
        UsersDto newUser = new UsersDto();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpassword");
        newUser.setGender(Gender.MALE);
        newUser.setRole(Role.USER);

        when(usersService.createUser(any(UsersDto.class))).thenReturn(newUser);

        // Act & Assert
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        verify(usersService, times(1)).createUser(any(UsersDto.class));
    }

    @Test
    @WithMockUser
    void updateUser_ShouldCallServiceWithCorrectId() throws Exception {
        // Arrange
        when(usersService.updateUser(eq(1), any(UsersDto.class))).thenReturn(usersDto);

        // Act
        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersDto)))
                .andExpect(status().isOk());

        // Assert
        verify(usersService, times(1)).updateUser(eq(1), any(UsersDto.class));
    }

    @Test
    @WithMockUser
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() throws Exception {
        // Arrange
        when(usersService.getAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getUserById_ShouldReturnCorrectUserData() throws Exception {
        // Arrange
        when(usersService.getUserById(1)).thenReturn(usersDto);

        // Act & Assert
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
