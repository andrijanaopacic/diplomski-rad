package ac.rs.bg.fon.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	@Value("${app.jwt.secret}")
	private String secret;
	
	@Value("${app.jwt.expiration-ms}")
	private long expirationMs;
	
	private Key key() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);
		
		return Jwts.builder().setSubject(user.getUsername())
							 .addClaims(extraClaims)
							 .setIssuedAt(now)
							 .setExpiration(exp)
							 .signWith(key(), SignatureAlgorithm.HS256)
							 .compact();
		
	}
	
	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(key())
								   .build()
								   .parseClaimsJws(token)
								   .getBody()
								   .getSubject();
	}
	
	public boolean isValid(String token, UserDetails user) {
		try {
			final String username = extractUsername(token);
			return username.equals(user.getUsername()) && !isExpired(token);
		} catch (JwtException e) {
			return false;
		}
	}
	
	private boolean isExpired(String token) {
		Date expiration = Jwts.parserBuilder().setSigningKey(key())
												.build()
												.parseClaimsJws(token)
												.getBody()
												.getExpiration();
		return expiration.before(new Date());
	}
	
	
}
