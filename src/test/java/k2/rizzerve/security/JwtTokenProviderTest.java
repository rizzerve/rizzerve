package k2.rizzerve.security;

import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwt;

    @BeforeEach
    void setup() {
        jwt = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwt, "validityInMs", 1_000L);
    }

    @Test
    void createAndValidateToken() {
        Long expectedId = 42L;
        String expectedUsername = "user";
        List<String> roles = List.of("ROLE_ADMIN");

        String token = jwt.createToken(expectedId, expectedUsername, roles);

        assertNotNull(token, "token should not be null");
        assertTrue(jwt.validateToken(token), "fresh token must validate");

        assertEquals(expectedId, jwt.getAdminId(token), "subject should round‑trip the id");

        assertEquals(expectedUsername, jwt.getUsername(token), "username claim should round‑trip");
    }

    @Test
    void expiredToken_isInvalid() throws InterruptedException {
        String token = jwt.createToken(1L, "u", List.of());

        Thread.sleep(1_100);

        assertFalse(jwt.validateToken(token), "token should be expired and invalid");
    }
}
