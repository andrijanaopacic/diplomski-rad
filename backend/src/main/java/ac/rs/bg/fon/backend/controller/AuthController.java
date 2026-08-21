package ac.rs.bg.fon.backend.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

import ac.rs.bg.fon.backend.dto.impl.AuthResponseDto;
import ac.rs.bg.fon.backend.dto.impl.KorisnikDto;
import ac.rs.bg.fon.backend.dto.impl.LoginRequestDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterOrganizacijaDto;
import ac.rs.bg.fon.backend.dto.impl.RegisterRequestDto;
import ac.rs.bg.fon.backend.dto.impl.RegistracijaResponseDto;
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
	public ResponseEntity<RegistracijaResponseDto> registerOrganizacija(@RequestBody RegisterOrganizacijaDto req) {
	    return ResponseEntity.ok(authService.registerOrganizacija(req));
	}
	
	@PostMapping("/register")
	public ResponseEntity<RegistracijaResponseDto> register(@RequestBody RegisterRequestDto req) {
	    return ResponseEntity.ok(authService.register(req));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto req) {
		return ResponseEntity.ok(authService.login(req));
	}
	
	@GetMapping("/verify")
	public ResponseEntity<Void> verify(@RequestParam String token) {
		try {
			authService.verifikujNalog(token);
			String poruka = "Uspešna registracija na sistem.";
			return redirect(frontendUrl + "/login?verified=success&poruka=" + encode(poruka));
		} catch (RuntimeException ex) {
			return redirect(frontendUrl + "/login?verified=error&poruka=" + encode(ex.getMessage()));
		}
	}
	
	private String encode(String tekst) {
		return URLEncoder.encode(tekst, StandardCharsets.UTF_8);
	}
	
	private ResponseEntity<Void> redirect(String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(url));
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
