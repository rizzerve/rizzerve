package k2.rizzerve.controller;

import k2.rizzerve.model.User;
import k2.rizzerve.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestParam String username, @RequestParam String email, @RequestParam String password) throws Exception {
        return authService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    public Optional<User> login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    @GetMapping("/profile/{username}")
    public Optional<User> getProfile(@PathVariable String username) {
        return authService.getProfile(username);
    }

    @PutMapping("/update/{id}")
    public Optional<User> updateProfile(@PathVariable Long id, @RequestParam String newName) {
        return authService.updateProfile(id, newName);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id) {
        return authService.deleteAccount(id) ? "Deleted" : "User not found";
    }
}
