package ac.rs.bg.fon.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.CreateUrednikDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;


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
	public ResponseEntity<KorisnikDto> createUrednik(@Valid @RequestBody CreateUrednikDto req) {
		return ResponseEntity.ok(korisnikService.createUrednik(req));
	}
}
