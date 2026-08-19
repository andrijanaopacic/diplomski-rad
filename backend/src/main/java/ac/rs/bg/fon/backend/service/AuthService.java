package ac.rs.bg.fon.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.AuthResponse;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.LoginRequest;
import ac.rs.bg.fon.backend.dto.impl.RegisterOrganizacijaDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterRequest;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.Uloga;
import ac.rs.bg.fon.backend.entity.impl.VerificationToken;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import ac.rs.bg.fon.backend.repository.impl.OrganizacijaRepository;
import ac.rs.bg.fon.backend.repository.impl.VerificationTokenRepository;
import ac.rs.bg.fon.backend.security.JwtProvider;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

	private static final long VERIFICATION_TTL_SECONDS = 24 * 60 * 60; 
	
	private final AuthenticationManager authManager;
	private final JwtProvider jwtProvider;
	private final KorisnikRepository korisnikRepository;
	private final OrganizacijaRepository organizacijaRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final PasswordEncoder encoder;
	private final MailService mailService;
	
	
	

	public AuthService(AuthenticationManager authManager, JwtProvider jwtProvider,
			KorisnikRepository korisnikRepository, OrganizacijaRepository organizacijaRepository,
			VerificationTokenRepository verificationTokenRepository, PasswordEncoder encoder, MailService mailService) {
		super();
		this.authManager = authManager;
		this.jwtProvider = jwtProvider;
		this.korisnikRepository = korisnikRepository;
		this.organizacijaRepository = organizacijaRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.encoder = encoder;
		this.mailService = mailService;
	}

	@Transactional
	public AuthResponse registerOrganizacija(RegisterOrganizacijaDto req) {
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			throw new IllegalArgumentException("Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			throw new IllegalArgumentException("Email adresa je zauzeta.");
		}
		if (organizacijaRepository.existsByPib(req.getPib())) {
			throw new IllegalArgumentException("Organizacija sa ovim PIB-om već postoji.");
		}
		if (organizacijaRepository.existsByMb(req.getMb())) {
			throw new IllegalArgumentException("Organizacija sa ovim matičnim brojem već postoji.");
		}
		
		Organizacija organizacija = new Organizacija();
		organizacija.setNaziv(req.getNazivOrganizacije());
		organizacija.setPib(req.getPib());
		organizacija.setMb(req.getMb());
		organizacija.setAdresa(req.getAdresa());
		organizacijaRepository.save(organizacija);
		
		Korisnik admin = new Korisnik();
		admin.setUsername(req.getUsername());
		admin.setEmail(req.getEmail());
		admin.setPasswordHash(encoder.encode(req.getPassword()));
		admin.setEnabled(false);
		admin.setUloga(Uloga.ADMIN);
		admin.setOrganizacija(organizacija);
		korisnikRepository.save(admin);
		
		posaljiVerifikacioniMejl(admin);
		
		return new AuthResponse(generateToken(admin), toDto(admin));
	}
	
	@Transactional
	public KorisnikDto register(RegisterRequest req) {
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			throw new IllegalArgumentException("Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			throw new IllegalArgumentException("Email adresa je zauzeta.");
		}
 
		Korisnik korisnik = new Korisnik();
		korisnik.setUsername(req.getUsername());
		korisnik.setEmail(req.getEmail());
		korisnik.setPasswordHash(encoder.encode(req.getPassword()));
		korisnik.setEnabled(true);
		korisnik.setUloga(Uloga.UCESNIK);
 
		korisnikRepository.save(korisnik);
		
		posaljiVerifikacioniMejl(korisnik);
 
		return toDto(korisnik);
	}
	
	public AuthResponse login(LoginRequest req) {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
 
		Korisnik korisnik = korisnikRepository.findByUsername(req.getUsername())
				.orElseThrow(() -> new IllegalStateException("Korisnik ne postoji."));
 
		return new AuthResponse(generateToken(korisnik), toDto(korisnik));
	}
 
	private String generateToken(Korisnik korisnik) {
		Map<String, Object> extraClaims = Map.of(
				"role", List.of(korisnik.getUloga().name())
		);
 
		return jwtProvider.generateToken(
				new User(
						korisnik.getUsername(),
						korisnik.getPasswordHash(),
						List.of(new SimpleGrantedAuthority("ROLE_" + korisnik.getUloga().name()))
				),
				extraClaims
		);
	}
	
	private KorisnikDto toDto(Korisnik korisnik) {
		return new KorisnikDto(
				korisnik.getKorisnikId(),
				korisnik.getUsername(),
				korisnik.getEmail(),
				korisnik.getUloga()
		);
	}
	
	@Transactional
	public void verifikujNalog(String token) {
		VerificationToken vt = verificationTokenRepository.findById(token)
				.orElseThrow(() -> new IllegalArgumentException("Neispravan token."));
 
		if (vt.isExpired()) {
			verificationTokenRepository.delete(vt);
			throw new IllegalArgumentException("Token je istekao. Zatraži novi.");
		}
 
		Korisnik korisnik = vt.getKorisnik();
		korisnik.setEnabled(true);
		korisnikRepository.save(korisnik);
 
		verificationTokenRepository.delete(vt);
	}
	
	private void posaljiVerifikacioniMejl(Korisnik korisnik) {
		VerificationToken vt = VerificationToken.of(korisnik, VERIFICATION_TTL_SECONDS);
		verificationTokenRepository.save(vt);
 
		String verifyUrl = "http://localhost:8080/api/auth/verify?token=" + vt.getToken();
 
		mailService.sendTemplatedHtml(
				korisnik.getEmail(),
				"Potvrda naloga",
				"verification-email.html",
				Map.of(
						"username", korisnik.getUsername(),
						"verifyUrl", verifyUrl
				)
		);
	}
	
	
 
}
