package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.UserRequestDto;
import com.avijeet.sprout.dto.UserResponseDto;
import com.avijeet.sprout.dto.mappers.UserMapper;
import com.avijeet.sprout.entities.User;
import com.avijeet.sprout.enums.Role;
import com.avijeet.sprout.exceptions.UserAlreadyExistsException;
import com.avijeet.sprout.exceptions.UserDoesNotExists;
import com.avijeet.sprout.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserRequestDto validRequest;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequestDto("avijeet@sprout.com", "SecurePass123");
        sampleUser = new User();
        sampleUser.setEmail("avijeet@sprout.com");
        sampleUser.setAccountNonLocked(true);
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void addUser_Success() {
        // Arrange
        when(userRepository.findByEmail(validRequest.email())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(userMapper.toDto(any(User.class))).thenReturn(new UserResponseDto(1L, "avijeet@sprout.com", "CUSTOMER", true));

        // Act
        UserResponseDto result = userService.addUser(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(validRequest.email(), result.email());

        // ArgumentCaptor to verify the entity state before saving
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("hashed_password", userCaptor.getValue().getPassword());
        assertEquals(Role.CUSTOMER, userCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("Should throw exception when email is already registered")
    void addUser_ThrowsException_IfUserExists() {
        // Arrange
        when(userRepository.findByEmail(validRequest.email())).thenReturn(sampleUser);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(validRequest));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should return user details when email exists")
    void getUserDetails_Success() {
        // Arrange
        when(userRepository.findByEmail("avijeet@sprout.com")).thenReturn(sampleUser);
        when(userMapper.toDto(sampleUser)).thenReturn(new UserResponseDto(1L, "avijeet@sprout.com", "CUSTOMER", true));

        // Act
        UserResponseDto result = userService.getUserDetailsByEmail("avijeet@sprout.com");

        // Assert
        assertEquals("avijeet@sprout.com", result.email());
    }

    @Test
    @DisplayName("Should throw exception when fetching non-existent user")
    void getUserDetails_ThrowsException_IfNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(UserDoesNotExists.class, () -> userService.getUserDetailsByEmail("ghost@sprout.com"));
    }

    @Test
    @DisplayName("Should successfully block an active account")
    void blockUserAccount_Success() {
        // Arrange: Start with an UNLOCKED user (true)
        sampleUser.setAccountNonLocked(true);
        when(userRepository.findByEmail("avijeet@sprout.com")).thenReturn(sampleUser);

        // Act
        boolean result = userService.blockUserAccount("avijeet@sprout.com");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        assertFalse(sampleUser.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should throw exception if blocking non-existent user")
    void blockUserAccount_ThrowsException_IfNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(UserDoesNotExists.class, () -> userService.blockUserAccount("none@sprout.com"));
    }
}