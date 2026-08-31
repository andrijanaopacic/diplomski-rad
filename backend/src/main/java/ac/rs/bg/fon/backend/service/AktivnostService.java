package ac.rs.bg.fon.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.dto.impl.AktivnostDto;
import ac.rs.bg.fon.backend.dto.impl.AktivnostSaFormomDto;
import ac.rs.bg.fon.backend.dto.impl.CreateAktivnostDto;
import ac.rs.bg.fon.backend.dto.impl.CreateDogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.DogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.FormaDto;
import ac.rs.bg.fon.backend.dto.impl.PoljeFormeDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Dogadjaj;
import ac.rs.bg.fon.backend.entity.impl.Forma;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.PoljeForme;
import ac.rs.bg.fon.backend.entity.impl.Prijava;
import ac.rs.bg.fon.backend.entity.impl.StatusPrijave;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.AktivnostRepository;
import ac.rs.bg.fon.backend.repository.impl.DogadjajRepository;
import ac.rs.bg.fon.backend.repository.impl.FormaRepository;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import ac.rs.bg.fon.backend.repository.impl.PrijavaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class AktivnostService {

	private final AktivnostRepository aktivnostRepository;
	private final DogadjajRepository dogadjajRepository;
	private final KorisnikRepository korisnikRepository;
	private final FormaRepository formaRepository;
	private final PrijavaRepository prijavaRepository;
	private final Validator validator;
	

	public AktivnostService(AktivnostRepository aktivnostRepository, DogadjajRepository dogadjajRepository,
			KorisnikRepository korisnikRepository, FormaRepository formaRepository, PrijavaRepository prijavaRepository,
			Validator validator) {
		super();
		this.aktivnostRepository = aktivnostRepository;
		this.dogadjajRepository = dogadjajRepository;
		this.korisnikRepository = korisnikRepository;
		this.formaRepository = formaRepository;
		this.prijavaRepository = prijavaRepository;
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
	
	private Dogadjaj ucitajSvojDogadjaj(Long dogadjajId, Korisnik trenutni) {
		Dogadjaj dogadjaj = dogadjajRepository.findById(dogadjajId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
 
		boolean istaOrganizacija = dogadjaj.getOrganizacija() != null
				&& dogadjaj.getOrganizacija().getOrganizacijaId()
						.equals(trenutni.getOrganizacija().getOrganizacijaId());
 
		if (!istaOrganizacija) {
			throw new RuntimeException("Sistem ne može da učita događaj.");
		}
 
		return dogadjaj;
	}
	
	 Aktivnost ucitajSvojuAktivnost(Long dogadjajId, Long aktivnostId, Korisnik trenutni) {
		Dogadjaj dogadjaj = ucitajSvojDogadjaj(dogadjajId, trenutni);
 
		Aktivnost aktivnost = aktivnostRepository.findById(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));
 
		if (!aktivnost.getDogadjaj().getDogadjajId().equals(dogadjaj.getDogadjajId())) {
			throw new RuntimeException("Sistem ne može da učita aktivnost.");
		}
 
		return aktivnost;
	}
	
	public ResponseDto<AktivnostDto> createAktivnost(Long dogadjajId, CreateAktivnostDto req) {
		String poruka = "Sistem ne može da zapamti aktivnost.";
		proveriValidnost(req, poruka);
 
		Korisnik trenutni = trenutniKorisnik();
		Dogadjaj dogadjaj = ucitajSvojDogadjaj(dogadjajId, trenutni);
 
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		if (aktivnostRepository.existsByNazivAndDogadjaj(req.getNaziv(), dogadjaj)) {
			fieldErrors.put("naziv", "Naziv aktivnosti je zauzet.");
		}
		if (!req.getRokZaPrijavu().isBefore(req.getDatumOdrzavanja())) {
			fieldErrors.put("rokZaPrijavu", "Rok za prijavu mora biti pre datuma održavanja.");
		}
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}
 
		Aktivnost aktivnost = new Aktivnost();
		aktivnost.setNaziv(req.getNaziv());
		aktivnost.setOpis(req.getOpis());
		aktivnost.setDatumOdrzavanja(req.getDatumOdrzavanja());
		aktivnost.setRokZaPrijavu(req.getRokZaPrijavu());
		aktivnost.setMaksUcesnika(req.getMaksUcesnika());
		aktivnost.setMestoOdrzavanja(req.getMestoOdrzavanja());
		aktivnost.setDogadjaj(dogadjaj);
		aktivnost.setKreirao(trenutni);
 
		aktivnostRepository.save(aktivnost);
 
		String uspesnaPoruka = "Sistem je zapamtio aktivnost.";
		return new ResponseDto<>(uspesnaPoruka, toDto(aktivnost));
	}
	
	public ResponseDto<List<AktivnostDto>> findAktivnosti(Long dogadjajId, String tekst) {
		Korisnik trenutni = trenutniKorisnik();
		Dogadjaj dogadjaj = ucitajSvojDogadjaj(dogadjajId, trenutni);
 
		List<Aktivnost> listaAktivnosti = aktivnostRepository.pretraziAktivnosti(dogadjaj, tekst);
 
		if (listaAktivnosti.isEmpty()) {
			throw new RuntimeException("Nema pronađenih aktivnosti.");
		}
 
		List<AktivnostDto> aktivnosti = new ArrayList<>();
		for (Aktivnost aktivnost : listaAktivnosti) {
			aktivnosti.add(toDto(aktivnost));
		}
 
		String poruka = "Sistem je našao aktivnosti po zadatom kriterijumu.";
		return new ResponseDto<>(poruka, aktivnosti);
	}
	
	public ResponseDto<AktivnostDto> loadAktivnost(Long dogadjajId, Long aktivnostId) {
		Korisnik trenutni = trenutniKorisnik();
		Dogadjaj dogadjaj = ucitajSvojDogadjaj(dogadjajId, trenutni);

		Aktivnost aktivnost = aktivnostRepository.findById(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));

		if (!aktivnost.getDogadjaj().getDogadjajId().equals(dogadjaj.getDogadjajId())) {
			throw new RuntimeException("Sistem ne može da učita aktivnost.");
		}

		String poruka = "Sistem je učitao aktivnost.";
		return new ResponseDto<>(poruka, toDto(aktivnost));
	}
	
	public ResponseDto<AktivnostDto> updateAktivnost(Long dogadjajId, Long aktivnostId, CreateAktivnostDto req) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);

		String poruka = "Sistem ne može da izmeni aktivnost.";
		
		proveriValidnost(req, poruka);

		Map<String, String> fieldErrors = new LinkedHashMap<>();
		if (!aktivnost.getNaziv().equals(req.getNaziv())
				&& aktivnostRepository.existsByNazivAndDogadjajAndAktivnostIdNot(req.getNaziv(), aktivnost.getDogadjaj(), aktivnostId)) {
			fieldErrors.put("naziv", "Naziv aktivnosti je zauzet.");
		}
		if (!req.getRokZaPrijavu().isBefore(req.getDatumOdrzavanja())) {
			fieldErrors.put("rokZaPrijavu", "Rok za prijavu mora biti pre datuma održavanja.");
		}
		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}

		aktivnost.setNaziv(req.getNaziv());
		aktivnost.setOpis(req.getOpis());
		aktivnost.setDatumOdrzavanja(req.getDatumOdrzavanja());
		aktivnost.setRokZaPrijavu(req.getRokZaPrijavu());
		aktivnost.setMaksUcesnika(req.getMaksUcesnika());
		aktivnost.setMestoOdrzavanja(req.getMestoOdrzavanja());

		aktivnostRepository.save(aktivnost);

		String uspesnaPoruka = "Aktivnost je izmenjena.";
		return new ResponseDto<>(uspesnaPoruka, toDto(aktivnost));
	}
	
	
	public PorukaResponseDto deleteAktivnost(Long dogadjajId, Long aktivnostId) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);

		if (formaRepository.existsByAktivnost(aktivnost)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "Aktivnost ima formu za prijavu, prvo obrišite formu.");
			throw new ValidacijaException("Sistem ne može da obriše aktivnost.", fieldErrors);
		}

		if (prijavaRepository.existsByAktivnost(aktivnost)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "Za aktivnost postoje prijave, ne može se obrisati.");
			throw new ValidacijaException("Sistem ne može da obriše aktivnost.", fieldErrors);
		}

		aktivnostRepository.delete(aktivnost);

		return new PorukaResponseDto("Aktivnost je obrisana.");
	}
	
	public ResponseDto<List<AktivnostDto>> findAktivnostiPublic(Long dogadjajId, String tekst) {
		Dogadjaj dogadjaj = dogadjajRepository.findById(dogadjajId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));
	 
		List<Aktivnost> listaAktivnosti = aktivnostRepository.pretraziAktivnosti(dogadjaj, tekst);
	 
		if (listaAktivnosti.isEmpty()) {
			throw new RuntimeException("Sistem ne može da nađe aktivnosti po zadatom kriterijumu.");
		}
	 
		List<AktivnostDto> aktivnosti = new ArrayList<>();
		for (Aktivnost aktivnost : listaAktivnosti) {
			aktivnosti.add(toDto(aktivnost));
		}
	 
		String poruka = "Sistem je našao aktivnosti po zadatom kriterijumu.";
		return new ResponseDto<>(poruka, aktivnosti);
	}
	
	public ResponseDto<AktivnostSaFormomDto> loadAktivnostPublic(Long dogadjajId, Long aktivnostId) {
		Dogadjaj dogadjaj = dogadjajRepository.findById(dogadjajId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita događaj."));

		Aktivnost aktivnost = aktivnostRepository.findById(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));

		if (!aktivnost.getDogadjaj().getDogadjajId().equals(dogadjaj.getDogadjajId())) {
			throw new RuntimeException("Sistem ne može da učita aktivnost.");
		}

		FormaDto formaDto = formaRepository.findByAktivnost(aktivnost)
				.map(this::toFormaDto)
				.orElse(null);

		Korisnik trenutni = trenutniKorisnik();
		StatusPrijave mojStatus = prijavaRepository
				.findByKorisnikAndAktivnostAndStatusPrijaveNot(trenutni, aktivnost, StatusPrijave.OTKAZANA)
				.map(Prijava::getStatusPrijave)
				.orElse(null);

		String poruka = "Sistem je učitao aktivnost.";
		return new ResponseDto<>(poruka, new AktivnostSaFormomDto(toDto(aktivnost), formaDto, mojStatus));
	}
	
	private FormaDto toFormaDto(Forma forma) {
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
 
	private AktivnostDto toDto(Aktivnost aktivnost) {
		return new AktivnostDto(
				aktivnost.getAktivnostId(),
				aktivnost.getNaziv(),
				aktivnost.getOpis(),
				aktivnost.getDatumOdrzavanja(),
				aktivnost.getRokZaPrijavu(),
				aktivnost.getMaksUcesnika(),
				aktivnost.getMestoOdrzavanja()
		);
	}
}
