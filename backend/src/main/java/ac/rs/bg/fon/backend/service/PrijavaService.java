package ac.rs.bg.fon.backend.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import ac.rs.bg.fon.backend.dto.impl.CreateOdgovorDto;
import ac.rs.bg.fon.backend.dto.impl.CreatePrijavaDto;
import ac.rs.bg.fon.backend.dto.impl.EvidentirajPrisustvoDto;
import ac.rs.bg.fon.backend.dto.impl.EvidentiranjeOdgovorDto;
import ac.rs.bg.fon.backend.dto.impl.OdgovorPregledDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaDetaljiDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaListaStavkaDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Forma;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Odgovor;
import ac.rs.bg.fon.backend.entity.impl.PoljeForme;
import ac.rs.bg.fon.backend.entity.impl.Prijava;
import ac.rs.bg.fon.backend.entity.impl.QRKod;
import ac.rs.bg.fon.backend.entity.impl.StatusPrijave;
import ac.rs.bg.fon.backend.entity.impl.TipPolja;
import ac.rs.bg.fon.backend.exception.ValidacijaException;
import ac.rs.bg.fon.backend.repository.impl.AktivnostRepository;
import ac.rs.bg.fon.backend.repository.impl.FormaRepository;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;
import ac.rs.bg.fon.backend.repository.impl.OdgovorRepository;
import ac.rs.bg.fon.backend.repository.impl.PrijavaRepository;
import ac.rs.bg.fon.backend.repository.impl.QRKodRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class PrijavaService {

	private static final long GRANICA_PROMOCIJE_SATI = 24;

	private final PrijavaRepository prijavaRepository;
	private final OdgovorRepository odgovorRepository;
	private final AktivnostRepository aktivnostRepository;
	private final FormaRepository formaRepository;
	private final QRKodRepository qrKodRepository;
	private final KorisnikRepository korisnikRepository;
	private final AktivnostService aktivnostService;
	private final MailService mailService;
	private final Validator validator;


	public PrijavaService(PrijavaRepository prijavaRepository, OdgovorRepository odgovorRepository,
			AktivnostRepository aktivnostRepository, FormaRepository formaRepository, QRKodRepository qrKodRepository,
			KorisnikRepository korisnikRepository, AktivnostService aktivnostService, MailService mailService,
			Validator validator) {
		super();
		this.prijavaRepository = prijavaRepository;
		this.odgovorRepository = odgovorRepository;
		this.aktivnostRepository = aktivnostRepository;
		this.formaRepository = formaRepository;
		this.qrKodRepository = qrKodRepository;
		this.korisnikRepository = korisnikRepository;
		this.aktivnostService = aktivnostService;
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

	private Korisnik trenutniKorisnik() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		return korisnikRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Ulogovan korisnik ne postoji u bazi."));
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public ResponseDto<PrijavaDto> addPrijava(Long aktivnostId, CreatePrijavaDto req) {
		String poruka = "Sistem ne može da zapamti prijavu.";
		proveriValidnost(req, poruka);

		Korisnik korisnik = trenutniKorisnik();

		Aktivnost aktivnost = aktivnostRepository.ucitajSaZakljucavanjem(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));
		

		if (prijavaRepository.existsByKorisnikAndAktivnostAndStatusPrijaveNot(
				korisnik, aktivnost, StatusPrijave.OTKAZANA)) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "Već ste prijavljeni na ovu aktivnost.");
			throw new ValidacijaException(poruka, fieldErrors);
		}

		if (LocalDateTime.now().isAfter(aktivnost.getRokZaPrijavu())) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "Rok za prijavu je istekao.");
			throw new ValidacijaException(poruka, fieldErrors);
		}

		Optional<Forma> formaOpt = formaRepository.findByAktivnost(aktivnost);
		List<Odgovor> odgovoriZaCuvanje = formaOpt.isPresent()
				? validirajIPripremiOdgovore(formaOpt.get(), req.getOdgovori(), poruka)
				: new ArrayList<>();

		long potvrdjenih = prijavaRepository.countByAktivnostAndStatusPrijave(aktivnost, StatusPrijave.POTVRDJENA);
		StatusPrijave status = potvrdjenih < aktivnost.getMaksUcesnika()
				? StatusPrijave.POTVRDJENA
				: StatusPrijave.NA_CEKANJU;

		Prijava prijava = new Prijava();
		prijava.setKorisnik(korisnik);
		prijava.setAktivnost(aktivnost);
		prijava.setDatumPrijave(LocalDateTime.now());
		prijava.setStatusPrijave(status);
		prijavaRepository.save(prijava);

		for (Odgovor odgovor : odgovoriZaCuvanje) {
			odgovor.setPrijava(prijava);
			odgovorRepository.save(odgovor);
		}

		boolean mejlPoslat;
		if (status == StatusPrijave.POTVRDJENA) {
			mejlPoslat = posaljiMejlSaQrKodom(prijava, "prijava-potvrdjena.html", "Prijava potvrđena");
		} else {
			mejlPoslat = posaljiMejlSigurno(() -> mailService.sendTemplatedHtml(
					korisnik.getEmail(),
					"Na listi ste čekanja",
					"prijava-cekanje.html",
					Map.of(
							"username", korisnik.getUsername(),
							"aktivnostNaziv", aktivnost.getNaziv()
					)
			));
		}

		String uspesnaPoruka = status == StatusPrijave.POTVRDJENA
				? "Sistem je zapamtio prijavu."
				: "Sistem je zapamtio prijavu. Mesta su popunjena, nalazite se na listi čekanja.";

		if (!mejlPoslat) {
			uspesnaPoruka += " Napomena: trenutno nismo uspeli da pošaljemo mejl potvrdu - proverite inbox kasnije.";
		}
		
		System.out.println("[" + Thread.currentThread().getName() + "] ZAVRSIO (otkljucava) u " + java.time.LocalTime.now());
		
		PrijavaDto dto = new PrijavaDto(prijava.getPrijavaId(), prijava.getDatumPrijave(), prijava.getStatusPrijave());
		return new ResponseDto<>(uspesnaPoruka, dto);
	
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public PorukaResponseDto cancelPrijava(Long aktivnostId) {
		String poruka = "Sistem ne može da otkaže prijavu.";

		Korisnik korisnik = trenutniKorisnik();

		Aktivnost aktivnost = aktivnostRepository.ucitajSaZakljucavanjem(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));
		

		Prijava prijava = prijavaRepository.findByKorisnikAndAktivnostAndStatusPrijaveNot(
				korisnik, aktivnost, StatusPrijave.OTKAZANA)
				.orElseThrow(() -> {
					Map<String, String> fieldErrors = new LinkedHashMap<>();
					fieldErrors.put("razlog", "Nemate aktivnu prijavu na ovu aktivnost.");
					return new ValidacijaException(poruka, fieldErrors);
				});

		boolean oslobodjenoMesto = prijava.getStatusPrijave() == StatusPrijave.POTVRDJENA;

		prijava.setStatusPrijave(StatusPrijave.OTKAZANA);
		prijavaRepository.save(prijava);

		boolean dovoljnoVremenaZaPromociju = LocalDateTime.now()
				.plusHours(GRANICA_PROMOCIJE_SATI)
				.isBefore(aktivnost.getDatumOdrzavanja());

		if (oslobodjenoMesto && dovoljnoVremenaZaPromociju) {
			prijavaRepository.findFirstByAktivnostAndStatusPrijaveOrderByDatumPrijaveAsc(
					aktivnost, StatusPrijave.NA_CEKANJU
			).ifPresent(sledeca -> {
				sledeca.setStatusPrijave(StatusPrijave.POTVRDJENA);
				prijavaRepository.save(sledeca);
				posaljiMejlSaQrKodom(sledeca, "prijava-promovisana.html", "Oslobodilo se mesto!");
			});
		}

		return new PorukaResponseDto("Prijava je otkazana.");
	}

	private boolean posaljiMejlSigurno(Runnable akcijaSlanja) {
		try {
			akcijaSlanja.run();
			return true;
		} catch (Exception ex) {
			System.err.println("Slanje mejla nije uspelo (prijava je sačuvana): " + ex.getMessage());
			return false;
		}
	}
	
	private boolean posaljiMejlSaQrKodom(Prijava prijava, String template, String naslov) {
		String kod = UUID.randomUUID().toString();

		QRKod qrKod = new QRKod();
		qrKod.setKod(kod);
		qrKod.setUrl("http://localhost:3000/potvrda/" + kod);
		qrKod.setIskoriscen(false);
		qrKod.setPrijava(prijava);
		qrKodRepository.save(qrKod);

		String qrSlikaBase64 = generisiQrSlikuBase64(kod);

		return posaljiMejlSigurno(() -> mailService.sendTemplatedHtml(
				prijava.getKorisnik().getEmail(),
				naslov,
				template,
				Map.of(
						"username", prijava.getKorisnik().getUsername(),
						"aktivnostNaziv", prijava.getAktivnost().getNaziv(),
						"qrKodBase64", qrSlikaBase64
				)
		));
	}

	private String generisiQrSlikuBase64(String tekst) {
		try {
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix matrica = writer.encode(tekst, BarcodeFormat.QR_CODE, 250, 250);
			ByteArrayOutputStream izlaz = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(matrica, "PNG", izlaz);
			return Base64.getEncoder().encodeToString(izlaz.toByteArray());
		} catch (WriterException | java.io.IOException ex) {
			throw new RuntimeException("Sistem ne može da generiše QR kod.", ex);
		}
	}

	private List<Odgovor> validirajIPripremiOdgovore(Forma forma, List<CreateOdgovorDto> odgovori, String poruka) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		List<Odgovor> rezultat = new ArrayList<>();

		Map<Long, String> poslatoMap = new LinkedHashMap<>();
		for (CreateOdgovorDto o : odgovori) {
			poslatoMap.put(o.getPoljeFormeId(), o.getVrednost());
		}

		Set<Long> validniIdovi = new HashSet<>();

		for (PoljeForme polje : forma.getPoljaForme()) {
			validniIdovi.add(polje.getPoljeFormeId());
			String vrednost = poslatoMap.get(polje.getPoljeFormeId());

			if (polje.isObavezno() && (vrednost == null || vrednost.isBlank())) {
				fieldErrors.put(polje.getNaziv(), "Ovo polje je obavezno.");
				continue;
			}

			if (vrednost != null && !vrednost.isBlank() && !odgovaraTipu(polje.getTip(), vrednost)) {
				fieldErrors.put(polje.getNaziv(), porukaZaTip(polje.getTip()));
				continue;
			}

			if (vrednost != null && !vrednost.isBlank()) {
				Odgovor odgovor = new Odgovor();
				odgovor.setForma(forma);
				odgovor.setPoljeForme(polje);
				odgovor.setVrednost(vrednost);
				rezultat.add(odgovor);
			}
		}

		for (Long poslatId : poslatoMap.keySet()) {
			if (!validniIdovi.contains(poslatId)) {
				fieldErrors.put("razlog", "Poslat je odgovor za polje koje ne pripada ovoj formi.");
			}
		}

		if (!fieldErrors.isEmpty()) {
			throw new ValidacijaException(poruka, fieldErrors);
		}

		return rezultat;
	}

	public ResponseDto<List<PrijavaListaStavkaDto>> findPrijave(Long dogadjajId, Long aktivnostId) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = aktivnostService.ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);
	 
		List<Prijava> sve = prijavaRepository.findByAktivnost(aktivnost);
		List<Prijava> aktivne = new ArrayList<>();
		for (Prijava p : sve) {
			if (p.getStatusPrijave() != StatusPrijave.OTKAZANA) {
				aktivne.add(p);
			}
		}
	 
		if (aktivne.isEmpty()) {
			throw new RuntimeException("Nema pronađenih prijava.");
		}
	 
		List<PrijavaListaStavkaDto> lista = new ArrayList<>();
		for (Prijava p : aktivne) {
			boolean dosao = qrKodRepository.findByPrijava(p)
					.map(QRKod::isIskoriscen)
					.orElse(false);
			lista.add(new PrijavaListaStavkaDto(
					p.getPrijavaId(),
					p.getKorisnik().getUsername(),
					p.getStatusPrijave(),
					dosao
			));
		}
	 
		String poruka = "Sistem je učitao prijave.";
		return new ResponseDto<>(poruka, lista);
	}
	 
	public PrijavaDetaljiDto loadPrijavu(Long dogadjajId, Long aktivnostId, Long prijavaId) {
		Korisnik trenutni = trenutniKorisnik();
		Aktivnost aktivnost = aktivnostService.ucitajSvojuAktivnost(dogadjajId, aktivnostId, trenutni);
	 
		Prijava prijava = prijavaRepository.findById(prijavaId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita prijavu."));
	 
		if (!prijava.getAktivnost().getAktivnostId().equals(aktivnost.getAktivnostId())) {
			throw new RuntimeException("Sistem ne može da učita prijavu.");
		}
	 
		boolean dosao = false;
		java.time.LocalDateTime vremeDolaska = null;
		var qrKodOpt = qrKodRepository.findByPrijava(prijava);
		if (qrKodOpt.isPresent()) {
			dosao = qrKodOpt.get().isIskoriscen();
			vremeDolaska = qrKodOpt.get().getVremeDolaska();
		}
	 
		List<Odgovor> odgovoriEntiteti = odgovorRepository.findByPrijava(prijava);
		List<OdgovorPregledDto> odgovori = new ArrayList<>();
		for (Odgovor o : odgovoriEntiteti) {
			odgovori.add(new OdgovorPregledDto(o.getPoljeForme().getNaziv(), o.getVrednost()));
		}
	 
		return new PrijavaDetaljiDto(
				prijava.getPrijavaId(),
				prijava.getKorisnik().getUsername(),
				prijava.getKorisnik().getEmail(),
				prijava.getStatusPrijave(),
				prijava.getDatumPrijave(),
				dosao,
				vremeDolaska,
				odgovori
		);
	}
	 
	
	@Transactional
	public EvidentiranjeOdgovorDto recordAttendance(EvidentirajPrisustvoDto req) {
		String poruka = "Sistem ne može da evidentira prisustvo.";
		proveriValidnost(req, poruka);

		QRKod qrKod = qrKodRepository.findByKod(req.getKod())
				.orElseThrow(() -> new RuntimeException("Sistem ne može da pronađe prijavu."));

		if (qrKod.isIskoriscen()) {
			Map<String, String> fieldErrors = new LinkedHashMap<>();
			fieldErrors.put("razlog", "QR kod je već iskorišćen.");
			throw new ValidacijaException(poruka, fieldErrors);
		}

		qrKod.setIskoriscen(true);
		qrKod.setVremeDolaska(LocalDateTime.now());
		qrKodRepository.save(qrKod);

		Long aktivnostId = qrKod.getPrijava().getAktivnost().getAktivnostId();
		return new EvidentiranjeOdgovorDto("Prisustvo je evidentirano.", aktivnostId);
	}
	
	public ResponseDto<List<PrijavaListaStavkaDto>> findPrijaveZaAktivnost(Long aktivnostId) {
		Aktivnost aktivnost = aktivnostRepository.findById(aktivnostId)
				.orElseThrow(() -> new RuntimeException("Sistem ne može da učita aktivnost."));
	 
		Long dogadjajId = aktivnost.getDogadjaj().getDogadjajId();
	 
		return findPrijave(dogadjajId, aktivnostId);
	}
	
	
	private boolean odgovaraTipu(TipPolja tip, String vrednost) {
		try {
			switch (tip) {
				case INTEGER:
					Integer.parseInt(vrednost.trim());
					return true;
				case DOUBLE:
					Double.parseDouble(vrednost.trim());
					return true;
				case LOCAL_DATE:
					LocalDate.parse(vrednost.trim());
					return true;
				case STRING:
				default:
					return true;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	private String porukaZaTip(TipPolja tip) {
		switch (tip) {
			case INTEGER: return "Vrednost mora biti ceo broj.";
			case DOUBLE: return "Vrednost mora biti decimalni broj.";
			case LOCAL_DATE: return "Vrednost mora biti datum u formatu GGGG-MM-DD.";
			default: return "Neispravna vrednost.";
		}
	}
}