package ac.rs.bg.fon.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.CreateFormaDto;
import ac.rs.bg.fon.backend.dto.impl.FormaDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniFormaDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniPoljeFormeDto;
import ac.rs.bg.fon.backend.dto.impl.PoljeFormeDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Dogadjaj;
import ac.rs.bg.fon.backend.entity.impl.Forma;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.PoljeForme;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.AktivnostRepository;
import ac.rs.bg.fon.backend.repository.impl.DogadjajRepository;
import ac.rs.bg.fon.backend.repository.impl.FormaRepository;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import ac.rs.bg.fon.backend.repository.impl.OdgovorRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class FormaService {

	private final FormaRepository formaRepository;
	private final AktivnostRepository aktivnostRepository;
	private final DogadjajRepository dogadjajRepository;
	private final KorisnikRepository korisnikRepository;
	private final OdgovorRepository odgovorRepository;
	private final Validator validator;
 

	public FormaService(FormaRepository formaRepository, AktivnostRepository aktivnostRepository,
			DogadjajRepository dogadjajRepository, KorisnikRepository korisnikRepository,
			OdgovorRepository odgovorRepository, Validator validator) {
		super();
		this.formaRepository = formaRepository;
		this.aktivnostRepository = aktivnostRepository;
		this.dogadjajRepository = dogadjajRepository;
		this.korisnikRepository = korisnikRepository;
		this.odgovorRepository = odgovorRepository;
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
	
	private Aktivnost ucitajSvojuAktivnost(Long dogadjajId, Long aktivnostId, Korisnik trenutni) {
		Dogadjaj dogadjaj = dogadjajRepository.findById(dogadjajId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
 
		boolean istaOrganizacija = dogadjaj.getOrganizacija() != null
				&& dogadjaj.getOrganizacija().getOrganizacijaId()
						.equals(trenutni.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija) {
			throw new RuntimeException("Sistem ne može da učita događaj.");
		}
 
		Aktivnost aktivnost = aktivnostRepository.findById(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));
 
		if (!aktivnost.getDogadjaj().getDogadjajId().equals(dogadjaj.getDogadjajId())) {
			throw new RuntimeException("Sistem ne može da učita aktivnost.");
		}
 
		return aktivnost;
	}
 
	public ResponseDto<FormaDto> addForma(Long dogadjajId, Long aktivnostId, CreateFormaDto req) {
		String poruka = "Sistem ne može da zapamti formu za prijavu.";
		proveriValidnost(req, poruka);
 
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);
 
		if (formaRepository.existsByAktivnost(aktivnost)) {
			throw new RuntimeException(poruka);
		}
 
		Forma forma = new Forma();
		forma.setNaziv(req.getNaziv());
		forma.setAktivnost(aktivnost);
 
		formaRepository.save(forma);
 
		String uspesnaPoruka = "Sistem je zapamtio formu za prijavu.";
		return new ResponseDto<>(uspesnaPoruka, toDto(forma));
	}
	
	public ResponseDto<FormaDto> loadFormu(Long dogadjajId, Long aktivnostId) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);

		Forma forma = formaRepository.findByAktivnost(aktivnost)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita formu za prijavu."));

		String poruka = "Sistem je učitao formu za prijavu.";
		return new ResponseDto<>(poruka, toDto(forma));
	}

	public ResponseDto<FormaDto> updateFormu(Long dogadjajId, Long aktivnostId, IzmeniFormaDto req) {
		String poruka = "Sistem ne može da izmeni formu za prijavu.";
		proveriValidnost(req, poruka);
 
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);
 
		Forma forma = formaRepository.findByAktivnost(aktivnost)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita formu za prijavu."));
 
		forma.setNaziv(req.getNaziv());
 
		Map<Long, PoljeForme> postojeca = new LinkedHashMap<>();
		for (PoljeForme p : forma.getPoljaForme()) {
			postojeca.put(p.getPoljeFormeId(), p);
		}
 
		Set<Long> poslatiIdovi = new HashSet<>();
 
		for (IzmeniPoljeFormeDto poljeDto : req.getPolja()) {
			if (poljeDto.getPoljeFormeId() != null && postojeca.containsKey(poljeDto.getPoljeFormeId())) {
				PoljeForme polje = postojeca.get(poljeDto.getPoljeFormeId());
				polje.setNaziv(poljeDto.getNaziv());
				polje.setObavezno(poljeDto.isObavezno());
				polje.setTip(poljeDto.getTip());
				poslatiIdovi.add(poljeDto.getPoljeFormeId());
			} else {
				PoljeForme novo = new PoljeForme();
				novo.setNaziv(poljeDto.getNaziv());
				novo.setObavezno(poljeDto.isObavezno());
				novo.setTip(poljeDto.getTip());
				novo.setForma(forma);
				forma.getPoljaForme().add(novo);
			}
		}
 
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		List<PoljeForme> zaUklanjanje = new ArrayList<>();
		for (PoljeForme p : postojeca.values()) {
			if (!poslatiIdovi.contains(p.getPoljeFormeId())) {
				if (odgovorRepository.existsByPoljeForme(p)) {
					fieldErrors.put(p.getNaziv(), "Ne može se ukloniti polje na koje već postoje odgovori.");
				} else {
					zaUklanjanje.add(p);
				}
			}
		}
 
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		forma.getPoljaForme().removeAll(zaUklanjanje);
 
		formaRepository.save(forma);
 
		String uspesnaPoruka = "Forma za prijavu je izmenjena.";
		return new ResponseDto<>(uspesnaPoruka, toDto(forma));
	}
	
	

	public PorukaResponseDto deleteFormu(Long dogadjajId, Long aktivnostId) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);

		Forma forma = formaRepository.findByAktivnost(aktivnost)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita formu za prijavu."));

		for (PoljeForme polje : forma.getPoljaForme()) {
			if (odgovorRepository.existsByPoljeForme(polje)) {
				Map<String, String> fieldErrors = new LinkedHashMap<>();
				fieldErrors.put("razlog", "Forma ima prijave sa odgovorima, ne može se obrisati.");
				throw new ValidacijaException("Sistem ne može da obriše formu za prijavu.", fieldErrors);
			}
		}

		formaRepository.delete(forma);

		return new PorukaResponseDto("Forma za prijavu je obrisana.");
	}
 
	private FormaDto toDto(Forma forma) {
		List<PoljeFormeDto> polja = new ArrayList<>();
		for (PoljeForme polje : forma.getPoljaForme()) {
			polja.add(new PoljeFormeDto(
					polje.getPoljeFormeId(),
					polje.getNaziv(),
					polje.isObavezno(),
					polje.getTip()
			));
		}
		return new FormaDto(forma.getFormaId(), forma.getNaziv(), polja);
	}
	
	
}
