package cc.oolong.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.Instant;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class JWTUtil {
    private final static String SECRET_KEY
            ="foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789";

    public String issueToken(String subject) {
        return issueToken(subject,Map.of());
    }

    public String issueToken(String subject, String ...roles) {
        return issueToken(subject,Map.of("roles",roles));
    }

    public String issueToken(String username, List<String> roles) {
        return issueToken(username,Map.of("roles",roles));
    }

    public String issueToken(String subject, Map<String,Object> claims) {
        String token=Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://www.oolong.cc")
                .setIssuedAt(Date.from(Instant.now()))
//                .setExpiration(Date.from(Instant.now().plus(30, SECONDS)))
                .setExpiration(Date.from(Instant.now().plus(15, DAYS)))
                .signWith(getSigningKey(), HS256)
                .compact();

        return token;

    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
        return claims;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String jwt, String username) {
        boolean isSameUser = getSubject(jwt).equals(username);
        return isSameUser && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }


}
