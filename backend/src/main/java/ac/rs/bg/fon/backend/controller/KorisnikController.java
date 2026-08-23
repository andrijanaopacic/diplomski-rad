package ac.rs.bg.fon.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.CreateUrednikDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniUrednikaDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.PretragaUrednikaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.RegistracijaResponseDto;
import ac.rs.bg.fon.backend.service.KorisnikService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/korisnik")
public class KorisnikController {

	private final KorisnikService korisnikService;
	 
	public KorisnikController(KorisnikService korisnikService) {
		this.korisnikService = korisnikService;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/urednik")
	public ResponseEntity<RegistracijaResponseDto> createUrednik(@RequestBody CreateUrednikDto req) {
		return ResponseEntity.ok(korisnikService.createUrednik(req));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/urednik/pretraga")
	public ResponseEntity<PretragaUrednikaResponseDto> pretraziUrednike(@RequestParam String tekst) {
	    return ResponseEntity.ok(korisnikService.pretraziUrednike(tekst));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/urednik/{id}")
	public ResponseEntity<KorisnikDto> ucitajUrednika(@PathVariable Long id){
		return ResponseEntity.ok(korisnikService.ucitajUrednika(id));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/urednik/{id}")
	public ResponseEntity<RegistracijaResponseDto> izmeniUrednika(@PathVariable Long id, @RequestBody IzmeniUrednikaDto req) {
	    return ResponseEntity.ok(korisnikService.izmeniUrednika(id, req));
	}
}
