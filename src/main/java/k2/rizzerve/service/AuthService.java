package k2.rizzerve.service;

import k2.rizzerve.model.User;
import k2.rizzerve.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(String username, String email, String rawPassword) throws Exception {
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new Exception("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, email, hashedPassword);
        user.addRole("USER");

        return userRepository.save(user);
    }

    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    public Optional<User> updateProfile(Long id, String newName) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(newName);
            return userRepository.save(user);
        });
    }

    public boolean deleteAccount(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> getProfile(String username) {
        return userRepository.findByUsername(username);
    }
}
