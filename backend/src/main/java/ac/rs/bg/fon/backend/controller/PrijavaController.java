package ac.rs.bg.fon.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.CreatePrijavaDto;
import ac.rs.bg.fon.backend.dto.impl.EvidentirajPrisustvoDto;
import ac.rs.bg.fon.backend.dto.impl.EvidentiranjeOdgovorDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaDetaljiDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaDto;
import ac.rs.bg.fon.backend.dto.impl.PrijavaListaStavkaDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.service.PrijavaService;

@RestController
public class PrijavaController {

	private final PrijavaService prijavaService;

	public PrijavaController(PrijavaService prijavaService) {
		this.prijavaService = prijavaService;
	}

	@PreAuthorize("hasRole('UCESNIK')")
	@PostMapping("/api/aktivnost/{aktivnostId}/prijava")
	public ResponseEntity<ResponseDto<PrijavaDto>> addPrijava(@PathVariable Long aktivnostId, @RequestBody CreatePrijavaDto req) {
		return ResponseEntity.ok(prijavaService.addPrijava(aktivnostId, req));
	}

	@PreAuthorize("hasRole('UCESNIK')")
	@DeleteMapping("/api/aktivnost/{aktivnostId}/prijava")
	public ResponseEntity<PorukaResponseDto> cancelPrijava(@PathVariable Long aktivnostId) {
		return ResponseEntity.ok(prijavaService.cancelPrijava(aktivnostId));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/api/dogadjaj/{dogadjajId}/aktivnost/{aktivnostId}/prijava")
	public ResponseEntity<ResponseDto<List<PrijavaListaStavkaDto>>> findPrijave(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(prijavaService.findPrijave(dogadjajId, aktivnostId));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/api/dogadjaj/{dogadjajId}/aktivnost/{aktivnostId}/prijava/{prijavaId}")
	public ResponseEntity<PrijavaDetaljiDto> loadPrijavu(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId, @PathVariable Long prijavaId) {
		return ResponseEntity.ok(prijavaService.loadPrijavu(dogadjajId, aktivnostId, prijavaId));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PostMapping("/api/evidentiranje-prisustva")
	public ResponseEntity<EvidentiranjeOdgovorDto> recordAttendance(@RequestBody EvidentirajPrisustvoDto req) {
		return ResponseEntity.ok(prijavaService.recordAttendance(req));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/api/aktivnost/{aktivnostId}/prijava")
	public ResponseEntity<ResponseDto<List<PrijavaListaStavkaDto>>> findPrijaveZaAktivnost(@PathVariable Long aktivnostId) {
		return ResponseEntity.ok(prijavaService.findPrijaveZaAktivnost(aktivnostId));
	}
}