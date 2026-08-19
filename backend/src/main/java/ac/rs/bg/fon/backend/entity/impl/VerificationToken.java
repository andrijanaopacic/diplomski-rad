package ac.rs.bg.fon.backend.entity.impl;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

	@Id
	private String token;
 
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "korisnik_id", nullable = false, unique = true)
	private Korisnik korisnik;
 
	@Column(nullable = false)
	private Instant expiresAt;
	
	public static VerificationToken of(Korisnik korisnik, long ttlSeconds) {
		VerificationToken t = new VerificationToken();
		t.token = UUID.randomUUID().toString();
		t.korisnik = korisnik;
		t.expiresAt = Instant.now().plusSeconds(ttlSeconds);
		return t;
	}
 
	public boolean isExpired() {
		return Instant.now().isAfter(expiresAt);
	}
}
