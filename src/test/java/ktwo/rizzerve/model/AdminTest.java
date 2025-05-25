package ktwo.rizzerve.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    @Test
    void gettersAndSetters() {
        Admin a = new Admin();
        a.setId(42L);
        a.setName("Alice");
        a.setUsername("alice");
        a.setPassword("secret");

        assertEquals(42L, a.getId());
        assertEquals("Alice", a.getName());
        assertEquals("alice", a.getUsername());
        assertEquals("secret", a.getPassword());
    }
}