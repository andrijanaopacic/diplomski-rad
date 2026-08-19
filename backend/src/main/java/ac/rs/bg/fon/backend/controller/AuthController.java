package ac.rs.bg.fon.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.rs.bg.fon.backend.dto.impl.AuthResponse;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.LoginRequest;
import ac.rs.bg.fon.backend.dto.impl.RegisterOrganizacijaDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterRequest;
import ac.rs.bg.fon.backend.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/register-organizacija")
	public ResponseEntity<AuthResponse> registerOrganizacija(@Valid @RequestBody RegisterOrganizacijaDto req) {
		return ResponseEntity.ok(authService.registerOrganizacija(req));
	}
	
	@PostMapping("/register")
	public ResponseEntity<KorisnikDto> register(@Valid @RequestBody RegisterRequest req) {
		return ResponseEntity.ok(authService.register(req));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
		return ResponseEntity.ok(authService.login(req));
	}
	
	@GetMapping("/verify")
	public ResponseEntity<String> verify(@RequestParam String token) {
		authService.verifikujNalog(token);
		return ResponseEntity.ok("Nalog je aktiviran. Sada možeš da se uloguješ.");
	}
}
