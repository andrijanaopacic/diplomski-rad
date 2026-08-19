package ac.rs.bg.fon.backend.entity.impl;

import java.util.Objects;

import ac.rs.bg.fon.backend.entity.MainEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "korisnik", uniqueConstraints = {
	    @UniqueConstraint(name = "uk_username", columnNames = "username"),
	    @UniqueConstraint(name = "uk_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik implements MainEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long korisnikId;
	
	@Column(nullable = false, length = 100)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String username;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@Column(nullable = false)
	private boolean enabled;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Uloga uloga = Uloga.UCESNIK;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organizacija_id")
	private Organizacija organizacija;

	public Korisnik(String email, String username, String passwordHash, boolean enabled, Uloga uloga) {
		super();
		this.email = email;
		this.username = username;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
		this.uloga = uloga;
	}
}
