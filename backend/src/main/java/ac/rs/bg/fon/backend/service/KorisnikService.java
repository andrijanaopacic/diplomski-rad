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
import ac.rs.bg.fon.backend.dto.impl.PretragaUrednikaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.RegistracijaResponseDto;
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
		super();
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
	
	public RegistracijaResponseDto createUrednik(CreateUrednikDto req) {
		proveriValidnost(req, "Sistem ne može da kreira urednika.");
		
		Korisnik admin = trenutniKorisnik();
		 
		if (korisnikRepository.existsByUsername(req.getUsername())) {
			throw new RuntimeException("Korisničko ime je zauzeto.");
		}
		if (korisnikRepository.existsByEmail(req.getEmail())) {
			throw new RuntimeException("Email adresa je zauzeta.");
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
 
		String poruka = "Sistem je zapamtio urednika.";
		KorisnikDto korisnikDto = new KorisnikDto(
				urednik.getKorisnikId(),
				urednik.getUsername(),
				urednik.getEmail(),
				urednik.getUloga(),
				urednik.isEnabled()
		);
		return new RegistracijaResponseDto(poruka, korisnikDto);
	}
	
	private Korisnik trenutniKorisnik() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		return korisnikRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Ulogovan korisnik ne postoji u bazi."));
	}
	
	public PretragaUrednikaResponseDto pretraziUrednike(String tekst) {
		Korisnik admin = trenutniKorisnik();
		Organizacija organizacija = admin.getOrganizacija();
		
		List<Korisnik> listaUrednika = korisnikRepository.pretraziUrednike(organizacija, tekst);
		
		if(listaUrednika.isEmpty()) {
			throw new RuntimeException("Sistem ne može da nađe urednike po zadatom kriterijumu.");
		}
		
		List<KorisnikDto> urednici = new ArrayList<KorisnikDto>();
		
		for (Korisnik urednik : listaUrednika) {
			KorisnikDto korisnikDto = new KorisnikDto(
					urednik.getKorisnikId(),
					urednik.getUsername(),
					urednik.getEmail(),
					urednik.getUloga(),
					urednik.isEnabled()
			);
			urednici.add(korisnikDto);
		}
		
		String poruka = "Sistem je našao urednike po zadatom kriterijumu.";
		return new PretragaUrednikaResponseDto(poruka, urednici);
	}
	
	public KorisnikDto ucitajUrednika(Long id) {
		Korisnik admin = trenutniKorisnik();
 
		Korisnik urednik = korisnikRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita urednika."));
 
		boolean istaOrganizacija = urednik.getOrganizacija() != null
				&& urednik.getOrganizacija().getOrganizacijaId().equals(admin.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija || urednik.getUloga() != Uloga.UREDNIK) {
			throw new RuntimeException("Sistem ne može da učita urednika.");
		}
 
		return new KorisnikDto(
				urednik.getKorisnikId(),
				urednik.getUsername(),
				urednik.getEmail(),
				urednik.getUloga(),
				urednik.isEnabled()
		);
	}
	
	public RegistracijaResponseDto izmeniUrednika(Long id, IzmeniUrednikaDto req) {
		Korisnik admin = trenutniKorisnik();
 
		Korisnik urednik = korisnikRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita urednika."));
 
		boolean istaOrganizacija = urednik.getOrganizacija() != null
				&& urednik.getOrganizacija().getOrganizacijaId().equals(admin.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija || urednik.getUloga() != Uloga.UREDNIK) {
			throw new RuntimeException("Sistem ne može da učita urednika.");
		}
 
		proveriValidnost(req, "Sistem ne može da izmeni urednika.");
 
		if (!urednik.getEmail().equals(req.getEmail())
				&& korisnikRepository.existsByEmail(req.getEmail())) {
			throw new RuntimeException("Email adresa je zauzeta.");
		}
 
		urednik.setEmail(req.getEmail());
		urednik.setEnabled(req.isEnabled());
 
		if (req.getPassword() != null && !req.getPassword().isBlank()) {
			if (req.getPassword().length() < 6) {
				throw new RuntimeException("Lozinka mora imati najmanje 6 karaktera.");
			}
			urednik.setPasswordHash(encoder.encode(req.getPassword()));
		}
 
		korisnikRepository.save(urednik);
 
		String poruka = "Urednik je izmenjen.";
		KorisnikDto korisnikDto = new KorisnikDto(
				urednik.getKorisnikId(),
				urednik.getUsername(),
				urednik.getEmail(),
				urednik.getUloga(),
				urednik.isEnabled()
		);
		return new RegistracijaResponseDto(poruka, korisnikDto);
	}
	
}
