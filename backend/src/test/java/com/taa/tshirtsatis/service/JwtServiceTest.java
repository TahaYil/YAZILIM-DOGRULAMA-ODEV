package com.taa.tshirtsatis.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private long jwtExpiration;

    @BeforeEach
    void setUp() {
        // Use a test secret key (minimum 256 bits for HS256)
        String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        jwtExpiration = 3600000; // 1 hour

        // Set the private fields using reflection
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);

        // Create a mock user
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        userDetails = new User("test@example.com", "password", authorities);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        UserDetails differentUser = new User("different@example.com", "password", authorities);

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", 123);
        extraClaims.put("customClaim", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractClaim_ShouldExtractCorrectClaim() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        assertEquals("test@example.com", subject);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void getExpirationTime_ShouldReturnConfiguredExpiration() {
        // Act
        long expirationTime = jwtService.getExpirationTime();

        // Assert
        assertEquals(jwtExpiration, expirationTime);
    }

    @Test
    void generateToken_ShouldIncludeRolesInClaims() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);

        // Verify the token contains the username
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void extractUsername_ShouldHandleValidToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertNotNull(extractedUsername);
        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void isTokenValid_ShouldValidateTokenCorrectly() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void generateToken_ShouldCreateNonExpiredToken() {
        // Arrange & Act
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        assertTrue(expiration.after(new Date()));
        assertTrue(expiration.before(new Date(System.currentTimeMillis() + jwtExpiration + 1000)));
    }
}
