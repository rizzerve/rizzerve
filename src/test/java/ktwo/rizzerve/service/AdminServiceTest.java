package ktwo.rizzerve.service;

import ktwo.rizzerve.model.Admin;
import ktwo.rizzerve.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock private AdminRepository repo;
    @Mock private BCryptPasswordEncoder encoder;
    @InjectMocks private AdminService svc;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_encodesPasswordAndSaves() {
        when(encoder.encode("raw")).thenReturn("enc");
        Admin saved = new Admin();
        saved.setId(1L);
        when(repo.save(any())).thenReturn(saved);

        Admin a = svc.register("N","U","raw");
        assertEquals(1L, a.getId());
        verify(encoder).encode("raw");
        verify(repo).save(any(Admin.class));
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(repo.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("x"));
    }

    @Test
    void updateProfile_changesNameAndUsername() {
        Admin a = new Admin();
        a.setName("old"); a.setUsername("u"); a.setPassword("p");
        when(repo.findByUsername("u")).thenReturn(Optional.of(a));
        when(repo.save(a)).thenReturn(a);

        Admin out = svc.updateProfile("u","new","newu");
        assertEquals("new", out.getName());
        assertEquals("newu", out.getUsername());
        verify(repo).save(a);
    }

    @Test
    void deleteByUsername_removes() {
        Admin a = new Admin();
        a.setUsername("u");
        when(repo.findByUsername("u")).thenReturn(Optional.of(a));
        svc.deleteByUsername("u");
        verify(repo).delete(a);
    }
}
