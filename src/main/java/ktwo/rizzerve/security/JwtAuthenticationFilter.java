package ktwo.rizzerve.security;

import ktwo.rizzerve.model.Admin;
import ktwo.rizzerve.service.AdminService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired private JwtTokenProvider jwtProvider;
    @Autowired private AdminService adminService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {
        String token = null;


        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        else if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwt".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }


        if (token != null && jwtProvider.validateToken(token)) {

            Long adminId = jwtProvider.getAdminId(token);

            Admin admin = adminService.findById(adminId);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            var userDetails = new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    "",
                    authorities
            );

            var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }
}
