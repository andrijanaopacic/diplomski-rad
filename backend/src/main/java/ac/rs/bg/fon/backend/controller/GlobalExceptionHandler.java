package ac.rs.bg.fon.backend.controller;

import org.springframework.security.access.AccessDeniedException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ac.rs.bg.fon.backend.exception.MyError;
import ac.rs.bg.fon.backend.exception.ValidacijaException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ValidacijaException.class)
	public ResponseEntity<MyError> handleValidacija(ValidacijaException ex) {
		return ResponseEntity.badRequest().body(new MyError(400, ex.getMessage(), ex.getFieldErrors()));
	}
 
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<MyError> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(403).body(new MyError(403, "Nemate dozvolu za ovu akciju."));
	}
 
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<MyError> handleRuntime(RuntimeException ex) {
		return ResponseEntity.badRequest().body(new MyError(400, ex.getMessage()));
	}
 
	@ExceptionHandler(Exception.class)
	public ResponseEntity<MyError> handleGeneric(Exception ex) {
		log.error("Neočekivana greška na serveru", ex);
		return ResponseEntity.internalServerError().body(new MyError(500, "Došlo je do neočekivane greške na serveru."));
	}
}
