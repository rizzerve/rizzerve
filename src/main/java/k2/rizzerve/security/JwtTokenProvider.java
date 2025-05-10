package k2.rizzerve.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${app.jwt.expiration-ms:900000}")
    private long validityInMs;

    /**
     * @param id        the admin’s database ID — this becomes the JWT subject
     * @param username  the admin’s username — carried in its own claim
     * @param roles     the admin’s roles
     */
    public String createToken(Long id, String username, List<String> roles) {
        Claims claims = Jwts.claims()
                .setSubject(id.toString());
        claims.put("username", username);
        claims.put("roles", roles);

        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * Parse out the admin ID (the JWT subject)
     */
    public Long getAdminId(String token) {
        String subj = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subj);
    }

    /**
     * Parse out the username claim
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    /** Signature & expiration check */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
