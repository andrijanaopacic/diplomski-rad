package ac.rs.bg.fon.backend.controller;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ac.rs.bg.fon.backend.exception.MyError;
import ac.rs.bg.fon.backend.exception.ValidacijaException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
		return ResponseEntity.internalServerError().body(new MyError(500, "Došlo je do neočekivane greške na serveru."));
	}
}
