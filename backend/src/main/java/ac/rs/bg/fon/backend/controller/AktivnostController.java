package ac.rs.bg.fon.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.AktivnostDto;
import ac.rs.bg.fon.backend.dto.impl.AktivnostSaFormomDto;
import ac.rs.bg.fon.backend.dto.impl.CreateAktivnostDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.service.AktivnostService;

@RestController
@RequestMapping("/api/dogadjaj/{dogadjajId}/aktivnost")
public class AktivnostController {

	private final AktivnostService aktivnostService;

	public AktivnostController(AktivnostService aktivnostService) {
		this.aktivnostService = aktivnostService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PostMapping
	public ResponseEntity<ResponseDto<AktivnostDto>> createAktivnost(@PathVariable Long dogadjajId, @RequestBody CreateAktivnostDto req) {
		return ResponseEntity.ok(aktivnostService.createAktivnost(dogadjajId, req));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/pretraga")
	public ResponseEntity<ResponseDto<List<AktivnostDto>>> findAktivnosti(@PathVariable Long dogadjajId, @RequestParam String tekst) {
		return ResponseEntity.ok(aktivnostService.findAktivnosti(dogadjajId, tekst));
	}
 
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/{aktivnostId}")
	public ResponseEntity<AktivnostDto> loadAktivnost(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(aktivnostService.loadAktivnost(dogadjajId, aktivnostId));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PutMapping("/{aktivnostId}")
	public ResponseEntity<ResponseDto<AktivnostDto>> updateAktivnost(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId, @RequestBody CreateAktivnostDto req) {
		return ResponseEntity.ok(aktivnostService.updateAktivnost(dogadjajId, aktivnostId, req));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@DeleteMapping("/{aktivnostId}")
	public ResponseEntity<PorukaResponseDto> deleteAktivnost(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(aktivnostService.deleteAktivnost(dogadjajId, aktivnostId));
	}
	

	@PreAuthorize("hasRole('UCESNIK')")
	@GetMapping("/javno/pretraga")
	public ResponseEntity<ResponseDto<List<AktivnostDto>>> findAktivnostiPublic(@PathVariable Long dogadjajId, @RequestParam String tekst) {
		return ResponseEntity.ok(aktivnostService.findAktivnostiPublic(dogadjajId, tekst));
	}
 
	@PreAuthorize("hasRole('UCESNIK')")
	@GetMapping("/javno/{aktivnostId}")
	public ResponseEntity<AktivnostSaFormomDto> loadAktivnostPublic(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(aktivnostService.loadAktivnostPublic(dogadjajId, aktivnostId));
	}
}
