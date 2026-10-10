package com.carmod.app.service;

import com.carmod.app.dto.UserRegistrationDto;
import com.carmod.app.entity.User;
import com.carmod.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setFullName("John Doe");
        registrationDto.setEmail("john@example.com");
        registrationDto.setPassword("Secret@123");
        registrationDto.setConfirmPassword("Secret@123");
        registrationDto.setPhone("+919876543210");
    }

    @Test
    @DisplayName("Should successfully register customer when email is unique and passwords match")
    void registerCustomer_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User savedUser = userService.registerCustomer(registrationDto);

        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("hashedPassword123", savedUser.getPassword());
        assertEquals(User.Role.CUSTOMER, savedUser.getRole());
        assertEquals(User.AccountStatus.ACTIVE, savedUser.getStatus());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void registerCustomer_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerCustomer(registrationDto);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when passwords do not match")
    void registerCustomer_PasswordMismatch_ThrowsException() {
        registrationDto.setConfirmPassword("DifferentPassword123");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerCustomer(registrationDto);
        });

        assertTrue(exception.getMessage().contains("Passwords do not match"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should toggle user status from ACTIVE to SUSPENDED and vice versa")
    void toggleUserStatus_Success() {
        User user = new User();
        user.setId(5L);
        user.setStatus(User.AccountStatus.ACTIVE);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.toggleUserStatus(5L);
        assertEquals(User.AccountStatus.SUSPENDED, user.getStatus());

        userService.toggleUserStatus(5L);
        assertEquals(User.AccountStatus.ACTIVE, user.getStatus());
    }
}
