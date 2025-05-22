package ktwo.rizzerve.service;

import ktwo.rizzerve.dto.AdminDto;
import ktwo.rizzerve.dto.AdminMapper;
import ktwo.rizzerve.model.Admin;
import ktwo.rizzerve.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements UserDetailsService {
    @Autowired private AdminRepository repo;
    @Autowired private BCryptPasswordEncoder encoder;

    public Admin register(String name, String username, String rawPassword) {
        Admin a = new Admin();
        a.setName(name);
        a.setUsername(username);
        a.setPassword(encoder.encode(rawPassword));
        return repo.save(a);
    }

    public Admin findByUsername(String u) {
        return repo.findByUsername(u)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + u));
    }

    public Admin updateProfile(String currentUsername,
                               String newName,
                               String newUsername) {
        Admin a = findByUsername(currentUsername);
        AdminDto update = new AdminDto(a.getId(), newName, newUsername);
        AdminMapper.updateEntity(a, update, encoder);
        return repo.save(a);
    }

    public void deleteByUsername(String username) {
        Admin a = findByUsername(username);
        repo.delete(a);
    }

    @Override
    public UserDetails loadUserByUsername(String u) throws UsernameNotFoundException {
        Admin a = findByUsername(u);
        return User.withUsername(a.getUsername())
                .password(a.getPassword())
                .roles("ADMIN")
                .build();
    }

    public Admin findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + id));
    }
}
