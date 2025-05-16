package k2.rizzerve.repository;

import k2.rizzerve.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public boolean existsByEmail(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter.getAndIncrement());
            users.add(user);
        } else {
            deleteById(user.getId());
            users.add(user);
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public boolean existsById(Long id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }

    public void deleteById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void deleteAll() { // Or clearAll()
        users.clear();
        idCounter.set(1); // Reset ID counter if desired
    }
}

