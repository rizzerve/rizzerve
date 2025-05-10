package k2.rizzerve.service;

import k2.rizzerve.model.User;
import k2.rizzerve.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Fix for tests: Set up a working encoder mock
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Fix: Mock the save method to return a proper User object
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser.getId() == null) {
                savedUser.setId(1L);
            }
            return savedUser;
        });
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        // Act
        User user = authService.registerUser(username, email, password);

        // Assert
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getPassword());
        assertTrue(user.getRoles().contains("USER"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        // Arrange
        String username = "existingUser";
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.registerUser(username, email, password);
        });
        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailExists() {
        // Arrange
        String username = "testuser";
        String email = "existing@example.com";
        String password = "password123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            authService.registerUser(username, email, password);
        });
        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        User mockUser = new User(username, "test@example.com", "encodedPassword");
        mockUser.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        // Fix: Ensure the password matching returns true
        when(passwordEncoder.matches(eq(password), anyString())).thenReturn(true);

        // Act
        Optional<User> result = authService.login(username, password);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    public void testLogin_WrongPassword() {
        // Arrange
        String username = "testuser";
        String password = "wrongPassword";
        User mockUser = new User(username, "test@example.com", "encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(eq(password), anyString())).thenReturn(false);

        // Act
        Optional<User> result = authService.login(username, password);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testLogin_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.login(username, password);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdateProfile_Success() {
        // Arrange
        Long userId = 1L;
        String newName = "updatedUsername";
        User originalUser = new User("oldUsername", "test@example.com", "password123");
        originalUser.setId(userId);
        
        User updatedUser = new User(newName, "test@example.com", "password123");
        updatedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        Optional<User> result = authService.updateProfile(userId, newName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(newName, result.get().getUsername());
    }

    @Test
    public void testUpdateProfile_UserNotFound() {
        // Arrange
        Long userId = 999L;
        String newName = "updatedUsername";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.updateProfile(userId, newName);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteAccount_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act
        boolean result = authService.deleteAccount(userId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteAccount_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        boolean result = authService.deleteAccount(userId);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    public void testGetProfile_Success() {
        // Arrange
        String username = "testuser";
        User mockUser = new User(username, "test@example.com", "password123");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> result = authService.getProfile(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    public void testGetProfile_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.getProfile(username);

        // Assert
        assertTrue(result.isEmpty());
    }
}
