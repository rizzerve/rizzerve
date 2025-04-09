package k2.rizzerve.repository;

import k2.rizzerve.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

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

    public void save(User user) {
        users.add(user);
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
}

