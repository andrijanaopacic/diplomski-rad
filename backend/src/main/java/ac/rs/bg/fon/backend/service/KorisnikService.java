package ac.rs.bg.fon.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.CreateUrednikDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniUrednikaDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.Uloga;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class KorisnikService {
	
	private final KorisnikRepository korisnikRepository;
	private final PasswordEncoder encoder;
	private final Validator validator;
	
	public KorisnikService(KorisnikRepository korisnikRepository, PasswordEncoder encoder, Validator validator) {
		this.korisnikRepository = korisnikRepository;
		this.encoder = encoder;
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
	
	public ResponseDto<KorisnikDto> createUrednik(CreateUrednikDto req) {
		String poruka = "Sistem ne može da kreira urednika.";
		proveriValidnost(req, poruka);
 
		Korisnik admin = trenutniKorisnik();
 
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
 
		Organizacija organizacija = admin.getOrganizacija();
 
		Korisnik urednik = new Korisnik();
		urednik.setUsername(req.getUsername());
		urednik.setEmail(req.getEmail());
		urednik.setPasswordHash(encoder.encode(req.getPassword()));
		urednik.setEnabled(true);
		urednik.setUloga(Uloga.UREDNIK);
		urednik.setOrganizacija(organizacija);
 
		korisnikRepository.save(urednik);
 
		String uspesnaPoruka = "Sistem je zapamtio urednika.";
		KorisnikDto korisnikDto = toDto(urednik);
		return new ResponseDto<>(uspesnaPoruka, toDto(urednik));
	}
 
	private Korisnik trenutniKorisnik() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		return korisnikRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Ulogovan korisnik ne postoji u bazi."));
	}
 
	public ResponseDto<List<KorisnikDto>> findUrednike(String tekst) {
		Korisnik admin = trenutniKorisnik();
		Organizacija organizacija = admin.getOrganizacija();
 
		List<Korisnik> listaUrednika = korisnikRepository.pretraziUrednike(organizacija, tekst);
 
		if (listaUrednika.isEmpty()) {
			throw new RuntimeException("Sistem ne može da nađe urednike po zadatom kriterijumu.");
		}
 
		List<KorisnikDto> urednici = new ArrayList<>();
		for (Korisnik urednik : listaUrednika) {
			urednici.add(toDto(urednik));
		}
 
		String poruka = "Sistem je našao urednike po zadatom kriterijumu.";
		return new ResponseDto<>(poruka, urednici);
	}
 
	public ResponseDto<KorisnikDto> loadUrednika(Long id) {
		Korisnik admin = trenutniKorisnik();

		Korisnik urednik = korisnikRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita urednika."));

		boolean istaOrganizacija = urednik.getOrganizacija() != null
				&& urednik.getOrganizacija().getOrganizacijaId().equals(admin.getOrganizacija().getOrganizacijaId());

		if (!istaOrganizacija || urednik.getUloga() != Uloga.UREDNIK) {
			throw new RuntimeException("Sistem ne može da učita urednika.");
		}

		String poruka = "Sistem je učitao urednika.";
		return new ResponseDto<>(poruka, toDto(urednik));
	}
 
	public ResponseDto<KorisnikDto> updateUrednika(Long id, IzmeniUrednikaDto req) {
		Korisnik admin = trenutniKorisnik();
 
		Korisnik urednik = korisnikRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita urednika."));
 
		boolean istaOrganizacija = urednik.getOrganizacija() != null
				&& urednik.getOrganizacija().getOrganizacijaId().equals(admin.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija || urednik.getUloga() != Uloga.UREDNIK) {
			throw new RuntimeException("Sistem ne može da učita urednika.");
		}
 
		String poruka = "Sistem ne može da izmeni urednika.";
		proveriValidnost(req, poruka);
 
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		if (!urednik.getEmail().equals(req.getEmail()) && korisnikRepository.existsByEmail(req.getEmail())) {
			fieldErrors.put("email", "Email adresa je zauzeta.");
		}
		if (req.getPassword() != null && !req.getPassword().isBlank() && req.getPassword().length() < 6) {
			fieldErrors.put("password", "Lozinka mora imati najmanje 6 karaktera.");
		}
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		urednik.setEmail(req.getEmail());
		urednik.setEnabled(req.isEnabled());
 
		if (req.getPassword() != null && !req.getPassword().isBlank()) {
			urednik.setPasswordHash(encoder.encode(req.getPassword()));
		}
 
		korisnikRepository.save(urednik);
 
		String uspesnaPoruka = "Urednik je izmenjen.";
		return new ResponseDto<>(uspesnaPoruka, toDto(urednik));
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
