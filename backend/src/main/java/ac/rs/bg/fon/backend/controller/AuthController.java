package ac.rs.bg.fon.backend.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
	
	@Value("${app.frontend.url}")
	private String frontendUrl;
	
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
	public ResponseEntity<Void> verify(@RequestParam String token) {
		try {
			authService.verifikujNalog(token);
			return redirect(frontendUrl + "/login?verified=success");
		} catch (IllegalArgumentException ex) {
			return redirect(frontendUrl + "/login?verified=error");
		}
	}
	
	private ResponseEntity<Void> redirect(String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(url));
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
