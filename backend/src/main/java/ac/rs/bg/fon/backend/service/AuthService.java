package ac.rs.bg.fon.backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.AuthResponseDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.LoginRequestDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterOrganizacijaDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterRequestDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.Uloga;
import ac.rs.bg.fon.backend.entity.impl.VerificationToken;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import ac.rs.bg.fon.backend.repository.impl.OrganizacijaRepository;
import ac.rs.bg.fon.backend.repository.impl.VerificationTokenRepository;
import ac.rs.bg.fon.backend.security.JwtProvider;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

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
	private final Validator validator;
 
	public AuthService(AuthenticationManager authManager, JwtProvider jwtProvider,
			KorisnikRepository korisnikRepository, OrganizacijaRepository organizacijaRepository,
			VerificationTokenRepository verificationTokenRepository, PasswordEncoder encoder,
			MailService mailService, Validator validator) {
		this.authManager = authManager;
		this.jwtProvider = jwtProvider;
		this.korisnikRepository = korisnikRepository;
		this.organizacijaRepository = organizacijaRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.encoder = encoder;
		this.mailService = mailService;
		this.validator = validator;
	}

	private <T> void proveriValidnost(T dto, String poruka) {
		Set<ConstraintViolation<T>> violations = validator.validate(dto);
		if (!violations.isEmpty()) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			for (ConstraintViolation<T> v : violations) {
				fieldErrors.put(v.getPropertyPath().toString(), v.getMessage());
			}
			throw new ValidacijaException(poruka, fieldErrors);
		}
	}
 
	private Long parsirajBroj(String vrednost, String nazivPolja, String porukaZaPolje, String poruka) {
		try {
			return Long.parseLong(vrednost.trim());
		} catch (NumberFormatException ex) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put(nazivPolja, porukaZaPolje);
			throw new ValidacijaException(poruka, fieldErrors);
		}
	}
	
	@Transactional
	public ResponseDto<KorisnikDto> registerOrganizacija(RegisterOrganizacijaDto req) {
		String poruka = "Sistem ne može da registruje organizaciju.";
		proveriValidnost(req, poruka);
 
		Long pib = parsirajBroj(req.getPib(), "pib", "PIB mora biti broj.", poruka);
		Long mb = parsirajBroj(req.getMb(), "mb", "Matični broj mora biti broj.", poruka);
 
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			fieldErrors.put("username", "Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			fieldErrors.put("email", "Email adresa je zauzeta.");
		}
		if (organizacijaRepository.existsByPib(pib)) {
			fieldErrors.put("pib", "Organizacija sa ovim PIB-om već postoji.");
		}
		if (organizacijaRepository.existsByMb(mb)) {
			fieldErrors.put("mb", "Organizacija sa ovim matičnim brojem već postoji.");
		}
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		Organizacija organizacija = new Organizacija();
		organizacija.setNaziv(req.getNazivOrganizacije());
		organizacija.setPib(pib);
		organizacija.setMb(mb);
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
 
		String uspesnaPoruka = "Registracija je pokrenuta. Proverite Vaš email radi potvrde naloga.";
		return new ResponseDto<>(uspesnaPoruka, toDto(admin));
	}
 
	@Transactional
	public ResponseDto<KorisnikDto> register(RegisterRequestDto req) {
		String poruka = "Sistem ne može da registruje korisnika.";
		proveriValidnost(req, poruka);
 
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			fieldErrors.put("username", "Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			fieldErrors.put("email", "Email adresa je zauzeta.");
		}
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		Korisnik korisnik = new Korisnik();
		korisnik.setUsername(req.getUsername());
		korisnik.setEmail(req.getEmail());
		korisnik.setPasswordHash(encoder.encode(req.getPassword()));
		korisnik.setEnabled(false);
		korisnik.setUloga(Uloga.UCESNIK);
 
		korisnikRepository.save(korisnik);
 
		posaljiVerifikacioniMejl(korisnik);
 
		String uspesnaPoruka = "Registracija je pokrenuta. Proverite Vaš email radi potvrde naloga.";
		return new ResponseDto<>(uspesnaPoruka, toDto(korisnik));
	}
 
	public AuthResponseDto login(LoginRequestDto req) {
		String poruka = "Sistem ne može da prijavi korisnika.";
		proveriValidnost(req, poruka);
 
		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
		} catch (BadCredentialsException | DisabledException ex) {
			throw new ValidacijaException(poruka, Map.of());
		}
 
		Korisnik korisnik = korisnikRepository.findByUsername(req.getUsername())
				.orElseThrow(() -> new ValidacijaException(poruka, Map.of()));
 
		return new AuthResponseDto(generateToken(korisnik), toDto(korisnik), "Uspešna prijava na sistem.");
	}
 
	@Transactional
	public void verifikujNalog(String token) {
		VerificationToken vt = verificationTokenRepository.findById(token)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da potvrdi nalog."));
 
		if (vt.isExpired()) {
			verificationTokenRepository.delete(vt);
			throw new RuntimeException("Sistem ne može da potvrdi nalog.");
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
				korisnik.getUloga(),
				korisnik.isEnabled()
		);
	}
	
 
}
