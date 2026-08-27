package ac.rs.bg.fon.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.CreateDogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.DogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Dogadjaj;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.AktivnostRepository;
import ac.rs.bg.fon.backend.repository.impl.DogadjajRepository;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class DogadjajService {

	private final DogadjajRepository dogadjajRepository;
	private final AktivnostRepository aktivnostRepository;
	private final KorisnikRepository korisnikRepository;
	private final Validator validator;


	public DogadjajService(DogadjajRepository dogadjajRepository, AktivnostRepository aktivnostRepository,
			KorisnikRepository korisnikRepository, Validator validator) {
		this.dogadjajRepository = dogadjajRepository;
		this.aktivnostRepository = aktivnostRepository;
		this.korisnikRepository = korisnikRepository;
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
	
	private Korisnik trenutniKorisnik() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		return korisnikRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Ulogovan korisnik ne postoji u bazi."));
	}
	
	public ResponseDto<DogadjajDto> createDogadjaj(CreateDogadjajDto req) {
		String poruka = "Sistem ne može da zapamti događaj.";
		proveriValidnost(req, poruka);
 
		Korisnik admin = trenutniKorisnik();
		Organizacija organizacija = admin.getOrganizacija();
 
		if (dogadjajRepository.existsByNazivAndOrganizacija(req.getNaziv(), organizacija)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("naziv", "Naziv događaja je zauzet.");
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		Dogadjaj dogadjaj = new Dogadjaj();
		dogadjaj.setKreirao(admin);
		dogadjaj.setNaziv(req.getNaziv());
		dogadjaj.setOpis(req.getOpis());
		dogadjaj.setOrganizacija(organizacija);
		dogadjaj.setSlika(req.getSlika());
 
		dogadjajRepository.save(dogadjaj);
 
		String uspesnaPoruka = "Sistem je zapamtio događaj.";
		DogadjajDto dogadjajDto = toDto(dogadjaj);
		return new ResponseDto<>(uspesnaPoruka, toDto(dogadjaj));
	}
 
	public ResponseDto<DogadjajDto> updateDogadjaj(Long id, CreateDogadjajDto req) {
		Korisnik trenutni = trenutniKorisnik();
 
		Dogadjaj dogadjaj = dogadjajRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
 
		boolean istaOrganizacija = dogadjaj.getOrganizacija() != null
				&& dogadjaj.getOrganizacija().getOrganizacijaId()
						.equals(trenutni.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija) {
			throw new RuntimeException("Sistem ne može da učita događaj.");
		}
 
		String poruka = "Sistem ne može da izmeni događaj.";
		proveriValidnost(req, poruka);
 
		if (!dogadjaj.getNaziv().equals(req.getNaziv())
				&& dogadjajRepository.existsByNazivAndOrganizacijaAndDogadjajIdNot(
						req.getNaziv(), dogadjaj.getOrganizacija(), id)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("naziv", "Naziv događaja je zauzet.");
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		dogadjaj.setNaziv(req.getNaziv());
		dogadjaj.setOpis(req.getOpis());
		dogadjaj.setSlika(req.getSlika());
 
		dogadjajRepository.save(dogadjaj);
 
		String uspesnaPoruka = "Događaj je izmenjen.";
		DogadjajDto dogadjajDto = toDto(dogadjaj);
		return new ResponseDto<>(uspesnaPoruka, toDto(dogadjaj));	}
	
	public ResponseDto<List<DogadjajDto>> findDogadjaje(String tekst) {
		Korisnik trenutni = trenutniKorisnik();
		Organizacija organizacija = trenutni.getOrganizacija();
 
		List<Dogadjaj> listaDogadjaja = dogadjajRepository.pretraziDogadjaje(organizacija, tekst);
 
		if (listaDogadjaja.isEmpty()) {
			throw new RuntimeException("Sistem ne može da nađe događaje po zadatom kriterijumu.");
		}
 
		List<DogadjajDto> dogadjaji = new ArrayList<>();
		for (Dogadjaj dogadjaj : listaDogadjaja) {
			dogadjaji.add(toDto(dogadjaj));
		}
 
		String poruka = "Sistem je našao događaje po zadatom kriterijumu.";
		return new ResponseDto<>(poruka, dogadjaji);
	}
	
	public PorukaResponseDto deleteDogadjaj(Long id) {
		Korisnik trenutni = trenutniKorisnik();

		Dogadjaj dogadjaj = dogadjajRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));

		boolean istaOrganizacija = dogadjaj.getOrganizacija() != null
				&& dogadjaj.getOrganizacija().getOrganizacijaId()
						.equals(trenutni.getOrganizacija().getOrganizacijaId());

		if (!istaOrganizacija) {
			throw new RuntimeException("Sistem ne može da učita događaj.");
		}
		
		if (aktivnostRepository.existsByDogadjaj(dogadjaj)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "Postoje aktivnosti vezane za njega.");
			throw new ValidacijaException("Sistem ne može da obriše događaj.", fieldErrors);
		}

		dogadjajRepository.delete(dogadjaj);

		return new PorukaResponseDto("Događaj je obrisan.");
	}
	
	public DogadjajDto loadDogadjaj(Long id) {
		Korisnik trenutni = trenutniKorisnik();
 
		Dogadjaj dogadjaj = dogadjajRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
 
		boolean istaOrganizacija = dogadjaj.getOrganizacija() != null
				&& dogadjaj.getOrganizacija().getOrganizacijaId()
						.equals(trenutni.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija) {
			throw new RuntimeException("Sistem ne može da učita događaj.");
		}
 
		return toDto(dogadjaj);
	}
	
	public ResponseDto<List<DogadjajDto>> findDogadjajePublic(String tekst) {
		List<Dogadjaj> listaDogadjaja = dogadjajRepository.javnaPretragaDogadjaja(tekst, LocalDateTime.now());
 
		if (listaDogadjaja.isEmpty()) {
			throw new RuntimeException("Sistem ne može da nađe događaje po zadatom kriterijumu.");
		}
 
		List<DogadjajDto> dogadjaji = new ArrayList<>();
		for (Dogadjaj dogadjaj : listaDogadjaja) {
			dogadjaji.add(toDto(dogadjaj));
		}
 
		String poruka = "Sistem je našao događaje po zadatom kriterijumu.";
		return new ResponseDto<>(poruka, dogadjaji);
	}
	
	public DogadjajDto loadDogadjajPublic(Long id) {
		Dogadjaj dogadjaj = dogadjajRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
 
		return toDto(dogadjaj);
	}
	
	
	private DogadjajDto toDto(Dogadjaj dogadjaj) {
	    return new DogadjajDto(
	            dogadjaj.getDogadjajId(),
	            dogadjaj.getNaziv(),
	            dogadjaj.getOpis(),
	            dogadjaj.getSlika(),
	            dogadjaj.getOrganizacija().getNaziv()
	    );
	}
}
