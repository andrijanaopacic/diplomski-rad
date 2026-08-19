package ac.rs.bg.fon.backend.controller;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ac.rs.bg.fon.backend.exception.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ApiError(400, ex.getMessage()));
	}
 
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity.badRequest().body(new ApiError(400, ex.getMessage()));
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(401, "Pogrešno korisničko ime ili lozinka."));
	}
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ApiError> handleDisabled(DisabledException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, "Nalog nije aktiviran. Proveri mejl za link za potvrdu."));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, "Nemaš dozvolu za ovu akciju."));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		String poruka = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getDefaultMessage())
				.collect(Collectors.joining(" "));
		return ResponseEntity.badRequest().body(new ApiError(400, poruka));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex) {
		return ResponseEntity.internalServerError()
				.body(new ApiError(500, "Došlo je do neočekivane greške na serveru."));
	}
}
