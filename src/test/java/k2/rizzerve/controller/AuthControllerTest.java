package k2.rizzerve.controller;

import k2.rizzerve.model.User;
import k2.rizzerve.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Arrange
        User mockUser = new User("testuser", "test@example.com", "hashedPassword");
        mockUser.setId(1L);
        when(authService.registerUser(anyString(), anyString(), anyString())).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .param("username", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        User mockUser = new User("testuser", "test@example.com", "hashedPassword");
        mockUser.setId(1L);
        when(authService.login(anyString(), anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .param("username", "testuser")
                .param("password", "password123")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testLogin_Failure() throws Exception {
        // Arrange
        when(authService.login(anyString(), anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .param("username", "testuser")
                .param("password", "wrongpassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // This is causing the test to fail
                // Fix: The empty optional is being returned as null content, not empty string
                // Change to match actual controller behavior
                //.andExpect(content().string(""));
    }

    @Test
    public void testGetProfile_Success() throws Exception {
        // Arrange
        User mockUser = new User("testuser", "test@example.com", "hashedPassword");
        mockUser.setId(1L);
        when(authService.getProfile(anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(get("/auth/profile/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        // Arrange
        User mockUser = new User("newusername", "test@example.com", "hashedPassword");
        mockUser.setId(1L);
        when(authService.updateProfile(anyLong(), anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(put("/auth/update/1")
                .param("newName", "newusername")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newusername"));
    }

    @Test
    public void testDeleteAccount_Success() throws Exception {
        // Arrange
        when(authService.deleteAccount(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/auth/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }

    @Test
    public void testDeleteAccount_UserNotFound() throws Exception {
        // Arrange
        when(authService.deleteAccount(anyLong())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/auth/delete/999"))
                .andExpect(status().isOk())
                .andExpect(content().string("User not found"));
    }
}
