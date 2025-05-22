package ktwo.rizzerve.repository;

import ktwo.rizzerve.model.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository repo;

    @Test
    void findByUsername_whenExists() {
        Admin a = new Admin();
        a.setName("Bob");
        a.setUsername("bob");
        a.setPassword("pw");
        repo.save(a);

        Optional<Admin> found = repo.findByUsername("bob");
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    void findByUsername_whenMissing() {
        assertTrue(repo.findByUsername("nope").isEmpty());
    }
}
