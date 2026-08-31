package ac.rs.bg.fon.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.CreateFormaDto;
import ac.rs.bg.fon.backend.dto.impl.FormaDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniFormaDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.service.FormaService;

@RestController
@RequestMapping("/api/dogadjaj/{dogadjajId}/aktivnost/{aktivnostId}/forma")
public class FormaController {

	private final FormaService formaService;

	public FormaController(FormaService formaService) {
		this.formaService = formaService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PostMapping
	public ResponseEntity<ResponseDto<FormaDto>> addForma(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId, @RequestBody CreateFormaDto req) {
		return ResponseEntity.ok(formaService.addForma(dogadjajId, aktivnostId, req));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping
	public ResponseEntity<ResponseDto<FormaDto>> loadFormu(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(formaService.loadFormu(dogadjajId, aktivnostId));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PutMapping
	public ResponseEntity<ResponseDto<FormaDto>> updateFormu(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId, @RequestBody IzmeniFormaDto req) {
		return ResponseEntity.ok(formaService.updateFormu(dogadjajId, aktivnostId, req));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@DeleteMapping
	public ResponseEntity<PorukaResponseDto> deleteFormu(@PathVariable Long dogadjajId, @PathVariable Long aktivnostId) {
		return ResponseEntity.ok(formaService.deleteFormu(dogadjajId, aktivnostId));
	}
}