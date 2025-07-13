package com.fintrack.users;

import com.fintrack.users.config.JwtTokenService;
import com.fintrack.users.dto.UserResponse;
import com.fintrack.users.model.User;
import com.fintrack.users.repository.UserRepository;
import com.fintrack.users.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("hashedpassword")
                .createdAt(new Date())
                .build();
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        SecurityContextHolder.setContext(securityContext);

        // Act
        UserResponse result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId().toString(), result.id());
        assertEquals(testUser.getUsername(), result.username());
        assertEquals(testUser.getEmail(), result.email());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistentuser");
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername("nonexistentuser");
    }

    @Test
    void testGetCurrentUser_NoAuthentication() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            userService.getCurrentUser();
        });

        assertTrue(exception.getMessage().contains("getName"));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testGetCurrentUser_NullAuthenticationName() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername(null);
    }
} 