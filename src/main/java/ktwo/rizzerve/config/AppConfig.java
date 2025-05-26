package ktwo.rizzerve.config;

import ktwo.rizzerve.security.JwtAuthenticationFilter;
import ktwo.rizzerve.security.JwtTokenProvider;
import ktwo.rizzerve.service.AdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtTokenProvider jwtProvider,
            AdminService adminService
    ) {
        return new JwtAuthenticationFilter(jwtProvider, adminService);
    }
}
