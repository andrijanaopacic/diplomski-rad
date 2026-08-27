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

import ac.rs.bg.fon.backend.dto.impl.CreateDogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.CreateUrednikDto;
import ac.rs.bg.fon.backend.dto.impl.DogadjajDto;
import ac.rs.bg.fon.backend.dto.impl.IzmeniUrednikaDto;
import ac.rs.bg.fon.backend.dto.impl.PorukaResponseDto;
import ac.rs.bg.fon.backend.dto.impl.ResponseDto;
import ac.rs.bg.fon.backend.service.DogadjajService;

@RestController
@RequestMapping("/api/dogadjaj")
public class DogadjajController {

	private final DogadjajService dogadjajService;

	public DogadjajController(DogadjajService dogadjajService) {
		this.dogadjajService = dogadjajService;
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PostMapping
	public ResponseEntity<ResponseDto<DogadjajDto>> createDogadjaj(@RequestBody CreateDogadjajDto req) {
		return ResponseEntity.ok(dogadjajService.createDogadjaj(req));
	}
 
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@PutMapping("/{id}")
	public ResponseEntity<ResponseDto<DogadjajDto>> updateDogadjaj(@PathVariable Long id, @RequestBody CreateDogadjajDto req) {
		return ResponseEntity.ok(dogadjajService.updateDogadjaj(id, req));
	}
 
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/{id}")
	public ResponseEntity<DogadjajDto> loadDogadjaj(@PathVariable Long id) {
		return ResponseEntity.ok(dogadjajService.loadDogadjaj(id));
	}
 
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@GetMapping("/pretraga")
	public ResponseEntity<ResponseDto<List<DogadjajDto>>> findDogadjaje(@RequestParam String tekst) {
		return ResponseEntity.ok(dogadjajService.findDogadjaje(tekst));
	}
 
	@PreAuthorize("hasAnyRole('ADMIN', 'UREDNIK')")
	@DeleteMapping("/{id}")
	public ResponseEntity<PorukaResponseDto> deleteDogadjaj(@PathVariable Long id) {
		return ResponseEntity.ok(dogadjajService.deleteDogadjaj(id));
	}
	
	@GetMapping("/javno/pretraga")
	public ResponseEntity<ResponseDto<List<DogadjajDto>>> findDogadjajePublic(@RequestParam String tekst) {
		return ResponseEntity.ok(dogadjajService.findDogadjajePublic(tekst));
	}
 
	@GetMapping("/javno/{id}")
	public ResponseEntity<DogadjajDto> loadDogadjajPublic(@PathVariable Long id) {
		return ResponseEntity.ok(dogadjajService.loadDogadjajPublic(id));
	}
}
