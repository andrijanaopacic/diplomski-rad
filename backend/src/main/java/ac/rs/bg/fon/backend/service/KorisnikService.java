package ac.rs.bg.fon.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.CreateUrednikDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.Uloga;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;

@Service
public class KorisnikService {
	
	private final KorisnikRepository korisnikRepository;
	private final PasswordEncoder encoder;
	
	public KorisnikService(KorisnikRepository korisnikRepository, PasswordEncoder encoder) {
		this.korisnikRepository = korisnikRepository;
		this.encoder = encoder;
	}
	
	public KorisnikDto createUrednik(CreateUrednikDto req) {
		Korisnik admin = trenutniKorisnik();
		 
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			throw new IllegalArgumentException("Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			throw new IllegalArgumentException("Email adresa je zauzeta.");
		}
 
		Organizacija organizacija = admin.getOrganizacija();
 
		Korisnik urednik = new Korisnik();
		urednik.setUsername(req.getUsername());
		urednik.setEmail(req.getEmail());
		urednik.setPasswordHash(encoder.encode(req.getPassword()));
		urednik.setEnabled(true);
		urednik.setUloga(Uloga.UREDNIK);
		urednik.setOrganizacija(organizacija);
 
		korisnikRepository.save(urednik);
 
		return new KorisnikDto(
				urednik.getKorisnikId(),
				urednik.getUsername(),
				urednik.getEmail(),
				urednik.getUloga()
		);
	}
	
	private Korisnik trenutniKorisnik() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		return korisnikRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException("Ulogovan korisnik ne postoji u bazi."));
	}
}
