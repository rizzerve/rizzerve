package ktwo.rizzerve.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import ktwo.rizzerve.dto.AdminDto;
import ktwo.rizzerve.dto.AdminMapper;
import ktwo.rizzerve.model.Admin;
import ktwo.rizzerve.security.JwtTokenProvider;
import ktwo.rizzerve.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService svc;
    private final JwtTokenProvider jwtProvider;
    private final AuthenticationManager authManager;

    public AdminController(
            AdminService svc,
            JwtTokenProvider jwtProvider,
            AuthenticationManager authManager
    ) {
        this.svc = svc;
        this.jwtProvider = jwtProvider;
        this.authManager = authManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AdminDto> register(@RequestBody AdminDto dto) {
        Admin admin = svc.register(dto.name, dto.username, dto.password);
        return new ResponseEntity<>(AdminMapper.toDto(admin), CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody AuthRequest req) {
        var auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password));
        UserDetails user = (UserDetails) auth.getPrincipal();

        Admin admin = svc.findByUsername(user.getUsername());

        String token = jwtProvider.createToken(
                admin.getId(),
                admin.getUsername(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }



    @GetMapping("/logout")
    public String logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        res.addCookie(cookie);

        return "redirect:/admin/login";
    }

    @GetMapping("/profile")
    public ResponseEntity<AdminDto> getProfile(@AuthenticationPrincipal UserDetails user) {
        Admin admin = svc.findByUsername(user.getUsername());
        return ResponseEntity.ok(AdminMapper.toDto(admin));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String,Object>> updateProfile(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody AdminDto dto) {

        Admin updated = svc.updateProfile(user.getUsername(), dto.name, dto.username);

        String newToken = jwtProvider.createToken(
                updated.getId(),
                updated.getUsername(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        return ResponseEntity.ok(Map.of(
                "admin", AdminMapper.toDto(updated),
                "token", newToken
        ));
    }


    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails user) {
        svc.deleteByUsername(user.getUsername());
        return new ResponseEntity<>(NO_CONTENT);
    }

    public static class AuthRequest {
        public String username;
        public String password;
    }
}